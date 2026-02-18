import { Metaplex, keypairIdentity, bundlrStorage, toMetaplexFile } from "@metaplex-foundation/js";
import { Connection, Keypair, clusterApiUrl, PublicKey, Transaction, TransactionInstruction, SystemProgram, SYSVAR_RENT_PUBKEY } from "@solana/web3.js";
import { TOKEN_PROGRAM_ID, ASSOCIATED_TOKEN_PROGRAM_ID, createAssociatedTokenAccountInstruction, getAssociatedTokenAddress, createInitializeMintInstruction, createMintToInstruction, MINT_SIZE, getMinimumBalanceForRentExemptMint } from "@solana/spl-token";
import fs from "fs";
import path from "path";

const METADATA_PROGRAM_ID = new PublicKey("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");

// Configuration
const CONFIG = {
  network: "devnet", // Change to "mainnet-beta" for production
  walletPath: "./wallet.json", // Path to your wallet keypair file
  imagePath: "./nft-image.png", // Path to your NFT image
  nftName: "My Master Edition NFT",
  nftSymbol: "MASTER",
  nftDescription: "This is a master edition NFT that can mint prints",
  externalUrl: "https://example.com",
  sellerFeeBasisPoints: 500, // 5% royalty
  maxSupply: 100, // Maximum prints (use null for unlimited)
  creators: [
    // Will be auto-populated with wallet address at 100% share
  ],
  attributes: [
    { trait_type: "Type", value: "Master Edition" },
    { trait_type: "Rarity", value: "Legendary" },
  ]
};

// Helper function to derive PDAs
function findProgramAddress(seeds, programId) {
  return PublicKey.findProgramAddressSync(seeds, programId);
}

// Setup Metaplex instance
async function setupMetaplex(network, wallet) {
  const endpoint = network === "mainnet-beta" 
    ? "https://api.mainnet-beta.solana.com" 
    : clusterApiUrl("devnet");
  
  const connection = new Connection(endpoint, "confirmed");
  
  const metaplex = Metaplex.make(connection)
    .use(keypairIdentity(wallet))
    .use(bundlrStorage({
      address: network === "mainnet-beta" 
        ? "https://node1.bundlr.network"
        : "https://devnet.bundlr.network",
      providerUrl: endpoint,
      timeout: 60000,
    }));

  return { metaplex, connection };
}

// Load wallet from file
function loadWallet(walletPath) {
  try {
    const secretKey = JSON.parse(fs.readFileSync(walletPath, "utf-8"));
    return Keypair.fromSecretKey(Uint8Array.from(secretKey));
  } catch (error) {
    console.error("Error loading wallet:", error.message);
    console.log("\nTo create a new wallet, run:");
    console.log("solana-keygen new --outfile ./wallet.json");
    process.exit(1);
  }
}

// Upload image to Arweave
async function uploadImage(metaplex, imagePath) {
  console.log("📤 Uploading image to Arweave...");
  
  const imageBuffer = fs.readFileSync(imagePath);
  const fileName = path.basename(imagePath);
  const fileExtension = path.extname(imagePath).slice(1);
  
  // Determine content type
  const contentTypeMap = {
    'png': 'image/png',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'gif': 'image/gif',
    'webp': 'image/webp',
  };
  const contentType = contentTypeMap[fileExtension.toLowerCase()] || 'image/png';
  
  const metaplexFile = toMetaplexFile(imageBuffer, fileName, {
    contentType: contentType,
  });

  const imageUri = await metaplex.storage().upload(metaplexFile);
  console.log("✅ Image uploaded:", imageUri);
  
  return imageUri;
}

// Upload metadata to Arweave
async function uploadMetadata(metaplex, imageUri, wallet) {
  console.log("📤 Uploading metadata to Arweave...");
  
  const metadata = {
    name: CONFIG.nftName,
    symbol: CONFIG.nftSymbol,
    description: CONFIG.nftDescription,
    seller_fee_basis_points: CONFIG.sellerFeeBasisPoints,
    image: imageUri,
    external_url: CONFIG.externalUrl,
    attributes: CONFIG.attributes,
    properties: {
      files: [
        {
          uri: imageUri,
          type: imageUri.endsWith('.png') ? 'image/png' : 'image/jpeg',
        },
      ],
      category: "image",
      creators: [
        {
          address: wallet.publicKey.toString(),
          share: 100,
        },
      ],
    },
  };

  const metadataUri = await metaplex.storage().uploadJson(metadata);
  console.log("✅ Metadata uploaded:", metadataUri);
  
  return { metadataUri, metadata };
}

// Create Master Edition NFT
async function createMasterEdition(metaplex, metadataUri, wallet) {
  console.log("\n🎨 Creating Master Edition NFT...");
  console.log("This may take a minute...");
  
  const { nft } = await metaplex.nfts().create({
    uri: metadataUri,
    name: CONFIG.nftName,
    symbol: CONFIG.nftSymbol,
    sellerFeeBasisPoints: CONFIG.sellerFeeBasisPoints,
    maxSupply: CONFIG.maxSupply ? BigInt(CONFIG.maxSupply) : null,
    creators: [
      {
        address: wallet.publicKey,
        share: 100,
      },
    ],
  });

  console.log("✅ Master Edition created!");
  console.log("   Mint address:", nft.address.toString());
  console.log("   Metadata address:", nft.metadataAddress.toString());
  console.log("   Master Edition address:", nft.edition.address.toString());
  console.log("   Max supply:", CONFIG.maxSupply || "Unlimited");
  
  return nft;
}

// Mint a print from the master edition
async function mintPrint(connection, wallet, masterMint, masterEdition, editionNumber) {
  console.log(`\n🖨️  Minting Print #${editionNumber}...`);
  
  const newMint = Keypair.generate();
  const newMintPubkey = newMint.publicKey;
  
  // Derive PDAs
  const [newMetadata] = findProgramAddress(
    [
      Buffer.from("metadata"),
      METADATA_PROGRAM_ID.toBuffer(),
      newMintPubkey.toBuffer(),
    ],
    METADATA_PROGRAM_ID
  );

  const [newEdition] = findProgramAddress(
    [
      Buffer.from("metadata"),
      METADATA_PROGRAM_ID.toBuffer(),
      newMintPubkey.toBuffer(),
      Buffer.from("edition"),
    ],
    METADATA_PROGRAM_ID
  );

  const [masterMetadata] = findProgramAddress(
    [
      Buffer.from("metadata"),
      METADATA_PROGRAM_ID.toBuffer(),
      masterMint.toBuffer(),
    ],
    METADATA_PROGRAM_ID
  );

  const [editionMarkPda] = findProgramAddress(
    [
      Buffer.from("metadata"),
      METADATA_PROGRAM_ID.toBuffer(),
      masterMint.toBuffer(),
      Buffer.from("edition"),
      Buffer.from(Math.floor(editionNumber / 248).toString()),
    ],
    METADATA_PROGRAM_ID
  );

  // Get or create token accounts
  const newTokenAccount = await getAssociatedTokenAddress(
    newMintPubkey,
    wallet.publicKey
  );

  const masterTokenAccount = await getAssociatedTokenAddress(
    masterMint,
    wallet.publicKey
  );

  const transaction = new Transaction();
  
  // Create new mint account
  const lamports = await getMinimumBalanceForRentExemptMint(connection);
  transaction.add(
    SystemProgram.createAccount({
      fromPubkey: wallet.publicKey,
      newAccountPubkey: newMintPubkey,
      space: MINT_SIZE,
      lamports,
      programId: TOKEN_PROGRAM_ID,
    })
  );

  // Initialize mint
  transaction.add(
    createInitializeMintInstruction(
      newMintPubkey,
      0,
      wallet.publicKey,
      wallet.publicKey,
      TOKEN_PROGRAM_ID
    )
  );

  // Create associated token account for new mint
  transaction.add(
    createAssociatedTokenAccountInstruction(
      wallet.publicKey,
      newTokenAccount,
      wallet.publicKey,
      newMintPubkey
    )
  );

  // Mint 1 token to the new account
  transaction.add(
    createMintToInstruction(
      newMintPubkey,
      newTokenAccount,
      wallet.publicKey,
      1
    )
  );

  // Create the mint print instruction
  const editionNumberBuffer = Buffer.alloc(8);
  editionNumberBuffer.writeBigUInt64LE(BigInt(editionNumber));
  
  const data = Buffer.concat([
    Buffer.from([14]), // MintNewEditionFromMasterEditionViaToken discriminator
    editionNumberBuffer,
  ]);

  const keys = [
    { pubkey: newMetadata, isSigner: false, isWritable: true },
    { pubkey: newEdition, isSigner: false, isWritable: true },
    { pubkey: masterEdition, isSigner: false, isWritable: true },
    { pubkey: newMintPubkey, isSigner: true, isWritable: true },
    { pubkey: editionMarkPda, isSigner: false, isWritable: true },
    { pubkey: wallet.publicKey, isSigner: true, isWritable: false }, // mint authority
    { pubkey: wallet.publicKey, isSigner: true, isWritable: false }, // payer
    { pubkey: wallet.publicKey, isSigner: true, isWritable: false }, // owner of master token account
    { pubkey: masterTokenAccount, isSigner: false, isWritable: false },
    { pubkey: wallet.publicKey, isSigner: false, isWritable: false }, // update authority
    { pubkey: masterMetadata, isSigner: false, isWritable: false },
    { pubkey: TOKEN_PROGRAM_ID, isSigner: false, isWritable: false },
    { pubkey: SystemProgram.programId, isSigner: false, isWritable: false },
    { pubkey: SYSVAR_RENT_PUBKEY, isSigner: false, isWritable: false },
  ];

  const mintPrintIx = new TransactionInstruction({
    keys,
    programId: METADATA_PROGRAM_ID,
    data,
  });

  transaction.add(mintPrintIx);

  // Send transaction
  transaction.feePayer = wallet.publicKey;
  const { blockhash } = await connection.getLatestBlockhash();
  transaction.recentBlockhash = blockhash;

  transaction.partialSign(newMint, wallet);
  
  const signature = await connection.sendRawTransaction(transaction.serialize(), {
    skipPreflight: false,
    preflightCommitment: "confirmed",
  });
  
  await connection.confirmTransaction(signature, "confirmed");

  console.log("✅ Print minted successfully!");
  console.log("   Edition number:", editionNumber);
  console.log("   New mint:", newMintPubkey.toString());
  console.log("   Transaction:", signature);
  console.log(`   View on explorer: https://explorer.solana.com/tx/${signature}?cluster=${CONFIG.network === "mainnet-beta" ? "mainnet" : "devnet"}`);
  
  return { mint: newMintPubkey, signature };
}

// Main function
async function main() {
  console.log("=".repeat(60));
  console.log("🚀 Metaplex Master Edition Creator & Print Minter");
  console.log("=".repeat(60));
  console.log(`Network: ${CONFIG.network}`);
  console.log(`Wallet: ${CONFIG.walletPath}`);
  console.log(`Image: ${CONFIG.imagePath}`);
  console.log("=".repeat(60));

  // Check if files exist
  if (!fs.existsSync(CONFIG.walletPath)) {
    console.error(`❌ Wallet file not found: ${CONFIG.walletPath}`);
    console.log("\nTo create a new wallet, run:");
    console.log("solana-keygen new --outfile ./wallet.json");
    process.exit(1);
  }

  if (!fs.existsSync(CONFIG.imagePath)) {
    console.error(`❌ Image file not found: ${CONFIG.imagePath}`);
    process.exit(1);
  }

  // Load wallet
  const wallet = loadWallet(CONFIG.walletPath);
  console.log(`\n💰 Wallet loaded: ${wallet.publicKey.toString()}`);

  // Setup Metaplex
  const { metaplex, connection } = await setupMetaplex(CONFIG.network, wallet);

  // Check balance
  const balance = await connection.getBalance(wallet.publicKey);
  console.log(`   Balance: ${(balance / 1e9).toFixed(4)} SOL`);
  
  if (balance < 0.1 * 1e9) {
    console.warn("\n⚠️  Warning: Low balance. You may need more SOL.");
    if (CONFIG.network === "devnet") {
      console.log("   Get devnet SOL: solana airdrop 2 " + wallet.publicKey.toString() + " --url devnet");
    }
  }

  try {
    // Step 1: Upload image
    const imageUri = await uploadImage(metaplex, CONFIG.imagePath);

    // Step 2: Upload metadata
    const { metadataUri, metadata } = await uploadMetadata(metaplex, imageUri, wallet);

    // Step 3: Create master edition
    const masterNft = await createMasterEdition(metaplex, metadataUri, wallet);

    // Save master edition info to file
    const masterEditionInfo = {
      network: CONFIG.network,
      mint: masterNft.address.toString(),
      metadata: masterNft.metadataAddress.toString(),
      masterEdition: masterNft.edition.address.toString(),
      name: CONFIG.nftName,
      symbol: CONFIG.nftSymbol,
      maxSupply: CONFIG.maxSupply,
      imageUri,
      metadataUri,
      createdAt: new Date().toISOString(),
    };

    fs.writeFileSync(
      "./master-edition-info.json",
      JSON.stringify(masterEditionInfo, null, 2)
    );
    console.log("\n💾 Master edition info saved to: ./master-edition-info.json");

    // Step 4: Mint some example prints
    console.log("\n" + "=".repeat(60));
    console.log("🖨️  Minting Example Prints");
    console.log("=".repeat(60));

    const numberOfPrints = 3; // Change this to mint more prints
    const prints = [];

    for (let i = 1; i <= numberOfPrints; i++) {
      const print = await mintPrint(
        connection,
        wallet,
        masterNft.address,
        masterNft.edition.address,
        i
      );
      prints.push(print);
      
      // Wait a bit between prints
      if (i < numberOfPrints) {
        await new Promise(resolve => setTimeout(resolve, 2000));
      }
    }

    // Save prints info
    const printsInfo = {
      masterMint: masterNft.address.toString(),
      prints: prints.map((p, i) => ({
        editionNumber: i + 1,
        mint: p.mint.toString(),
        signature: p.signature,
      })),
    };

    fs.writeFileSync(
      "./prints-info.json",
      JSON.stringify(printsInfo, null, 2)
    );
    console.log("\n💾 Prints info saved to: ./prints-info.json");

    console.log("\n" + "=".repeat(60));
    console.log("✨ All done!");
    console.log("=".repeat(60));
    console.log(`\n🔗 View your Master Edition on explorer:`);
    console.log(`https://explorer.solana.com/address/${masterNft.address.toString()}?cluster=${CONFIG.network === "mainnet-beta" ? "mainnet" : "devnet"}`);

  } catch (error) {
    console.error("\n❌ Error:", error.message);
    if (error.logs) {
      console.error("Transaction logs:", error.logs);
    }
    throw error;
  }
}

// Run if called directly
if (import.meta.url === `file://${process.argv[1]}`) {
  main().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}

export { createMasterEdition, mintPrint, uploadImage, uploadMetadata };
