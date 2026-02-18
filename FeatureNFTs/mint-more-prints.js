import { Connection, Keypair, PublicKey } from "@solana/web3.js";
import { mintPrint } from "./metaplex-master-edition.js";
import fs from "fs";

/**
 * Mint Additional Prints Script
 * 
 * Use this script to mint more prints after your Master Edition is created.
 * It reads the master-edition-info.json file created by the main script.
 */

async function main() {
  console.log("=".repeat(60));
  console.log("🖨️  Mint Additional Prints");
  console.log("=".repeat(60));

  // Load master edition info
  if (!fs.existsSync("./master-edition-info.json")) {
    console.error("❌ master-edition-info.json not found!");
    console.log("   Run the main script first to create a Master Edition.");
    process.exit(1);
  }

  const masterInfo = JSON.parse(fs.readFileSync("./master-edition-info.json", "utf-8"));
  console.log(`\n📋 Master Edition: ${masterInfo.mint}`);
  console.log(`   Network: ${masterInfo.network}`);
  console.log(`   Max Supply: ${masterInfo.maxSupply || "Unlimited"}`);

  // Load wallet
  const walletPath = masterInfo.network === "mainnet-beta" 
    ? "./mainnet-wallet.json" 
    : "./wallet.json";
  
  if (!fs.existsSync(walletPath)) {
    console.error(`❌ Wallet file not found: ${walletPath}`);
    process.exit(1);
  }

  const wallet = Keypair.fromSecretKey(
    Uint8Array.from(JSON.parse(fs.readFileSync(walletPath, "utf-8")))
  );
  console.log(`   Wallet: ${wallet.publicKey.toString()}`);

  // Setup connection
  const endpoint = masterInfo.network === "mainnet-beta"
    ? "https://api.mainnet-beta.solana.com"
    : "https://api.devnet.solana.com";
  
  const connection = new Connection(endpoint, "confirmed");

  // Check balance
  const balance = await connection.getBalance(wallet.publicKey);
  console.log(`   Balance: ${(balance / 1e9).toFixed(4)} SOL`);

  if (balance < 0.01 * 1e9) {
    console.error("\n❌ Insufficient balance! Need at least 0.01 SOL per print.");
    process.exit(1);
  }

  // Load existing prints to determine next edition number
  let nextEdition = 1;
  if (fs.existsSync("./prints-info.json")) {
    const printsInfo = JSON.parse(fs.readFileSync("./prints-info.json", "utf-8"));
    if (printsInfo.prints && printsInfo.prints.length > 0) {
      nextEdition = Math.max(...printsInfo.prints.map(p => p.editionNumber)) + 1;
    }
  }

  console.log(`\n   Next edition number: ${nextEdition}`);

  // Configuration
  const START_EDITION = nextEdition; // Start from next available edition
  const NUMBER_OF_PRINTS = 5; // How many prints to mint
  const BATCH_SIZE = 3; // How many to process at once

  console.log(`   Minting ${NUMBER_OF_PRINTS} prints starting from edition ${START_EDITION}`);
  
  // Check if we'll exceed max supply
  if (masterInfo.maxSupply && (START_EDITION + NUMBER_OF_PRINTS - 1 > masterInfo.maxSupply)) {
    console.error(`\n❌ Error: This would exceed max supply of ${masterInfo.maxSupply}`);
    console.log(`   Can only mint up to edition ${masterInfo.maxSupply}`);
    process.exit(1);
  }

  console.log("\n" + "=".repeat(60));

  const prints = [];
  const masterMint = new PublicKey(masterInfo.mint);
  const masterEdition = new PublicKey(masterInfo.masterEdition);

  // Mint prints in batches
  for (let i = 0; i < NUMBER_OF_PRINTS; i += BATCH_SIZE) {
    const batchPromises = [];
    
    for (let j = 0; j < BATCH_SIZE && (i + j) < NUMBER_OF_PRINTS; j++) {
      const editionNumber = START_EDITION + i + j;
      
      batchPromises.push(
        mintPrint(connection, wallet, masterMint, masterEdition, editionNumber)
          .then(result => {
            prints.push({
              editionNumber,
              mint: result.mint.toString(),
              signature: result.signature,
            });
            return result;
          })
          .catch(error => {
            console.error(`\n❌ Failed to mint edition ${editionNumber}:`, error.message);
            return null;
          })
      );
    }

    // Wait for batch to complete
    await Promise.all(batchPromises);

    // Small delay between batches
    if (i + BATCH_SIZE < NUMBER_OF_PRINTS) {
      console.log("\n⏳ Waiting before next batch...\n");
      await new Promise(resolve => setTimeout(resolve, 2000));
    }
  }

  // Update prints-info.json
  let printsInfo = { masterMint: masterInfo.mint, prints: [] };
  if (fs.existsSync("./prints-info.json")) {
    printsInfo = JSON.parse(fs.readFileSync("./prints-info.json", "utf-8"));
  }

  printsInfo.prints = [...printsInfo.prints, ...prints];
  printsInfo.prints.sort((a, b) => a.editionNumber - b.editionNumber);

  fs.writeFileSync("./prints-info.json", JSON.stringify(printsInfo, null, 2));

  console.log("\n" + "=".repeat(60));
  console.log("✨ Minting Complete!");
  console.log("=".repeat(60));
  console.log(`\n📊 Summary:`);
  console.log(`   Successfully minted: ${prints.length} prints`);
  console.log(`   Edition range: ${START_EDITION} - ${START_EDITION + prints.length - 1}`);
  console.log(`   Total prints minted: ${printsInfo.prints.length}`);
  
  if (masterInfo.maxSupply) {
    console.log(`   Remaining supply: ${masterInfo.maxSupply - printsInfo.prints.length}`);
  }

  console.log(`\n💾 Updated prints-info.json`);
  
  console.log(`\n🔗 View on explorer:`);
  const clusterParam = masterInfo.network === "mainnet-beta" ? "" : "?cluster=devnet";
  console.log(`   Master Edition: https://explorer.solana.com/address/${masterInfo.mint}${clusterParam}`);
}

// Run the script
main().catch((error) => {
  console.error("\n❌ Error:", error.message);
  console.error(error);
  process.exit(1);
});
