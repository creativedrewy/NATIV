# Metaplex Master Edition Creator & Print Minter

Complete solution for creating Metaplex NFT Master Editions with automatic Arweave uploads and print minting using raw Solana transactions.

## Features

- ✅ Upload images to Arweave via Bundlr
- ✅ Upload metadata to Arweave
- ✅ Create Master Edition NFTs with the Metaplex JS SDK
- ✅ Mint prints using raw Solana transactions
- ✅ Support for both devnet and mainnet
- ✅ Configurable max supply, royalties, and attributes
- ✅ Automatic JSON output for tracking created NFTs

## Prerequisites

- Node.js 16+ installed
- Solana CLI tools installed (optional but recommended)

## Installation

### 1. Initialize your project

```bash
mkdir metaplex-master-edition
cd metaplex-master-edition
npm init -y
```

### 2. Install dependencies

```bash
npm install @metaplex-foundation/js @solana/web3.js @solana/spl-token
```

### 3. Configure package.json for ES modules

Add this to your `package.json`:

```json
{
  "type": "module"
}
```

### 4. Copy the script

Copy the `metaplex-master-edition.js` file to your project directory.

## Setup for Devnet

### Step 1: Create a wallet

```bash
# Using Solana CLI (recommended)
solana-keygen new --outfile ./wallet.json

# Or create manually using Node.js
node -e "console.log(JSON.stringify(Array.from(require('@solana/web3.js').Keypair.generate().secretKey)))" > wallet.json
```

### Step 2: Fund your wallet

```bash
# Get your wallet address
solana address -k ./wallet.json

# Request airdrop (2 SOL)
solana airdrop 2 <YOUR_WALLET_ADDRESS> --url devnet

# You may need to run this multiple times to get enough SOL
# Repeat until you have at least 2-3 SOL for testing
```

### Step 3: Prepare your image

Place your NFT image in the project directory:

```bash
# Your image should be named nft-image.png
# Supported formats: PNG, JPG, JPEG, GIF, WEBP
cp /path/to/your/image.png ./nft-image.png
```

### Step 4: Configure the script

Edit the `CONFIG` object in `metaplex-master-edition.js`:

```javascript
const CONFIG = {
  network: "devnet",  // Keep as devnet for testing
  walletPath: "./wallet.json",
  imagePath: "./nft-image.png",
  nftName: "My Master Edition NFT",
  nftSymbol: "MASTER",
  nftDescription: "This is a master edition NFT that can mint prints",
  externalUrl: "https://example.com",
  sellerFeeBasisPoints: 500, // 5% royalty
  maxSupply: 100, // Maximum prints (use null for unlimited)
  attributes: [
    { trait_type: "Type", value: "Master Edition" },
    { trait_type: "Rarity", value: "Legendary" },
  ]
};
```

### Step 5: Run the script

```bash
node metaplex-master-edition.js
```

### Step 6: Verify on Solana Explorer

The script will output URLs to view your NFTs on Solana Explorer (devnet):

```
https://explorer.solana.com/address/<MINT_ADDRESS>?cluster=devnet
```

## Setup for Mainnet

⚠️ **IMPORTANT**: Mainnet operations cost real SOL. Test thoroughly on devnet first!

### Step 1: Use a funded mainnet wallet

```bash
# Create a new wallet for mainnet (SECURELY!)
solana-keygen new --outfile ./mainnet-wallet.json

# Transfer SOL to this wallet from your existing wallet/exchange
# You'll need approximately 0.5-1 SOL for:
# - Arweave storage (~0.1-0.3 SOL depending on file size)
# - Account creation and transactions (~0.01-0.05 SOL)
# - Buffer for gas fees

# Check your balance
solana balance -k ./mainnet-wallet.json --url mainnet-beta
```

### Step 2: Update configuration for mainnet

Edit `metaplex-master-edition.js`:

```javascript
const CONFIG = {
  network: "mainnet-beta",  // Changed from devnet
  walletPath: "./mainnet-wallet.json",  // Use your mainnet wallet
  imagePath: "./nft-image.png",
  // ... rest of your configuration
};
```

### Step 3: Double-check everything

Before running on mainnet, verify:

- ✅ Your image is the correct one
- ✅ Metadata (name, description, attributes) is accurate
- ✅ Royalty percentage is correct
- ✅ Max supply is set correctly
- ✅ Wallet has sufficient SOL (check with `solana balance`)
- ✅ You've tested the complete flow on devnet

### Step 4: Run on mainnet

```bash
node metaplex-master-edition.js
```

### Step 5: Verify on Solana Explorer

Check your NFTs on mainnet explorer:

```
https://explorer.solana.com/address/<MINT_ADDRESS>
```

You can also view on popular NFT marketplaces:
- Magic Eden: `https://magiceden.io/item-details/<MINT_ADDRESS>`
- Tensor: `https://tensor.trade/item/<MINT_ADDRESS>`

## Output Files

The script creates two JSON files:

### master-edition-info.json
Contains details about your Master Edition:

```json
{
  "network": "devnet",
  "mint": "...",
  "metadata": "...",
  "masterEdition": "...",
  "name": "My Master Edition NFT",
  "symbol": "MASTER",
  "maxSupply": 100,
  "imageUri": "https://arweave.net/...",
  "metadataUri": "https://arweave.net/...",
  "createdAt": "2024-02-07T..."
}
```

### prints-info.json
Contains details about minted prints:

```json
{
  "masterMint": "...",
  "prints": [
    {
      "editionNumber": 1,
      "mint": "...",
      "signature": "..."
    }
  ]
}
```

## Configuration Options

### Network Settings

```javascript
network: "devnet"        // For testing
network: "mainnet-beta"  // For production
```

### Supply Settings

```javascript
maxSupply: 100    // Limited to 100 prints
maxSupply: null   // Unlimited prints
```

### Royalty Settings

```javascript
sellerFeeBasisPoints: 500  // 5% royalty
sellerFeeBasisPoints: 1000 // 10% royalty
sellerFeeBasisPoints: 0    // No royalty
```

### Attributes

```javascript
attributes: [
  { trait_type: "Background", value: "Blue" },
  { trait_type: "Rarity", value: "Rare" },
  { trait_type: "Edition", value: "First" },
]
```

## Minting Additional Prints

To mint more prints after initial creation:

```javascript
import { Connection, Keypair, PublicKey } from "@solana/web3.js";
import { mintPrint } from "./metaplex-master-edition.js";
import fs from "fs";

// Load master edition info
const masterInfo = JSON.parse(fs.readFileSync("./master-edition-info.json"));
const wallet = Keypair.fromSecretKey(
  Uint8Array.from(JSON.parse(fs.readFileSync("./wallet.json")))
);

const connection = new Connection(
  masterInfo.network === "mainnet-beta"
    ? "https://api.mainnet-beta.solana.com"
    : "https://api.devnet.solana.com"
);

// Mint print #4
await mintPrint(
  connection,
  wallet,
  new PublicKey(masterInfo.mint),
  new PublicKey(masterInfo.masterEdition),
  4  // Edition number
);
```

## Cost Breakdown

### Devnet (Free)
- All operations are free
- SOL can be obtained via airdrop

### Mainnet (Approximate Costs)
- **Arweave Upload** (via Bundlr):
  - Image (< 1MB): ~0.1-0.2 SOL
  - Metadata: ~0.01 SOL
- **Master Edition Creation**: ~0.01-0.02 SOL
- **Per Print Mint**: ~0.005-0.01 SOL
- **Total for Master + 10 Prints**: ~0.3-0.5 SOL

*Costs vary based on network congestion and file sizes*

## Troubleshooting

### "Insufficient funds" error

**Solution**: Add more SOL to your wallet
```bash
# Devnet
solana airdrop 2 <YOUR_ADDRESS> --url devnet

# Mainnet
# Transfer SOL from exchange or another wallet
```

### "Image file not found" error

**Solution**: Make sure your image file exists at the specified path
```bash
ls -la nft-image.png
```

### "Failed to upload to Arweave" error

**Solutions**:
1. Check your internet connection
2. Ensure you have enough SOL for storage
3. Try again (Bundlr can be temporarily unavailable)
4. For large files, increase timeout in the script

### "Invalid edition number" error

**Solution**: Make sure you're not exceeding maxSupply and that edition numbers are sequential (or at least not already minted)

### "Transaction simulation failed" error

**Solutions**:
1. Check that you own the Master Edition token
2. Verify the Master Edition hasn't reached max supply
3. Ensure sufficient SOL for transaction fees
4. Check that edition number isn't already taken

## Security Best Practices

### For Mainnet

1. **Never share your wallet private key**
2. **Use a dedicated wallet** for NFT creation (not your main wallet)
3. **Test on devnet first** - always validate your entire workflow
4. **Backup your wallet file** securely (encrypted, offline)
5. **Double-check configurations** before running on mainnet
6. **Keep minimal SOL** in your hot wallet - only what you need for operations
7. **Use hardware wallets** for high-value operations when possible

### Wallet File Storage

```bash
# Good: Store securely, limited permissions
chmod 600 wallet.json

# Bad: Don't commit to git
echo "*.json" >> .gitignore
echo "wallet.json" >> .gitignore
echo "mainnet-wallet.json" >> .gitignore
```

## Advanced Usage

### Custom RPC Endpoints

For better performance, use a custom RPC provider:

```javascript
const endpoint = network === "mainnet-beta" 
  ? "https://your-custom-rpc-endpoint.com"  // Use QuickNode, Helius, etc.
  : clusterApiUrl("devnet");
```

Recommended RPC providers:
- [QuickNode](https://www.quicknode.com/)
- [Helius](https://www.helius.dev/)
- [Triton](https://triton.one/)

### Batch Minting Prints

Modify the script to mint multiple prints efficiently:

```javascript
// In main function
const numberOfPrints = 50; // Mint 50 prints
const batchSize = 5; // Process 5 at a time

for (let i = 1; i <= numberOfPrints; i += batchSize) {
  const batch = [];
  for (let j = i; j < i + batchSize && j <= numberOfPrints; j++) {
    batch.push(mintPrint(connection, wallet, masterNft.address, masterNft.edition.address, j));
  }
  await Promise.all(batch);
  await new Promise(resolve => setTimeout(resolve, 2000)); // Wait between batches
}
```

## Resources

- [Metaplex Documentation](https://docs.metaplex.com/)
- [Solana Documentation](https://docs.solana.com/)
- [Metaplex JS SDK](https://github.com/metaplex-foundation/js)
- [Bundlr Network](https://bundlr.network/)
- [Solana Explorer](https://explorer.solana.com/)

## License

MIT

## Support

For issues or questions:
1. Check this README for solutions
2. Review Metaplex documentation
3. Test on devnet before reporting issues
4. Include full error messages and transaction signatures when seeking help

---

**Remember**: Always test thoroughly on devnet before deploying to mainnet!
