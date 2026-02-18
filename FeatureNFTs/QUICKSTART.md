# Quick Start Guide

## 🚀 Devnet Quick Start (5 minutes)

### 1. Setup Project

```bash
# Create project directory
mkdir my-master-edition && cd my-master-edition

# Initialize npm (accept all defaults)
npm init -y

# Install dependencies
npm install @metaplex-foundation/js @solana/web3.js @solana/spl-token

# Enable ES modules
echo '{"type": "module"}' > temp.json
node -e "const pkg = require('./package.json'); const temp = require('./temp.json'); Object.assign(pkg, temp); require('fs').writeFileSync('package.json', JSON.stringify(pkg, null, 2));"
rm temp.json
```

### 2. Create Wallet

```bash
# Option A: Using Solana CLI (recommended)
solana-keygen new --outfile ./wallet.json

# Option B: Using Node.js
node -e "console.log(JSON.stringify(Array.from((await import('@solana/web3.js')).Keypair.generate().secretKey)))" > wallet.json
```

### 3. Fund Wallet

```bash
# Get your wallet address
solana address -k ./wallet.json

# Request airdrop (run multiple times to get 2-3 SOL)
solana airdrop 2 $(solana address -k ./wallet.json) --url devnet
solana airdrop 2 $(solana address -k ./wallet.json) --url devnet

# Check balance
solana balance -k ./wallet.json --url devnet
```

### 4. Add Your Image

```bash
# Copy your image to the project directory
cp /path/to/your/image.png ./nft-image.png

# Or download a sample image
curl -o nft-image.png https://placekitten.com/800/800
```

### 5. Copy the Script

Save `metaplex-master-edition.js` to your project directory.

### 6. Run!

```bash
node metaplex-master-edition.js
```

### 7. View Results

The script will output:
- Master Edition mint address
- Transaction signatures
- Explorer links

Check your NFTs:
- `https://explorer.solana.com/address/<MINT_ADDRESS>?cluster=devnet`

---

## 💎 Mainnet Quick Start

### Prerequisites

✅ Successfully tested on devnet
✅ Have 0.5-1 SOL ready to use
✅ Image and metadata finalized

### 1. Create Mainnet Wallet

```bash
# Create a new secure wallet
solana-keygen new --outfile ./mainnet-wallet.json

# Get the address
solana address -k ./mainnet-wallet.json
```

### 2. Fund Wallet

Transfer 0.5-1 SOL to the address from your exchange or existing wallet.

```bash
# Check balance
solana balance -k ./mainnet-wallet.json --url mainnet-beta
```

### 3. Update Configuration

Edit `metaplex-master-edition.js`, change:

```javascript
const CONFIG = {
  network: "mainnet-beta",  // ← Change this
  walletPath: "./mainnet-wallet.json",  // ← Change this
  // ... rest stays the same
};
```

### 4. Final Checklist

- [ ] Image is correct and high quality
- [ ] NFT name and description are final
- [ ] Royalty percentage is correct (in basis points: 500 = 5%)
- [ ] Max supply is set correctly
- [ ] Wallet has at least 0.5 SOL
- [ ] Tested complete flow on devnet

### 5. Run on Mainnet

```bash
node metaplex-master-edition.js
```

### 6. Verify

Check on mainnet explorer:
- `https://explorer.solana.com/address/<MINT_ADDRESS>`

View on marketplaces:
- Magic Eden: `https://magiceden.io/item-details/<MINT_ADDRESS>`
- Tensor: `https://tensor.trade/item/<MINT_ADDRESS>`

---

## ⚡ Customization Examples

### Unlimited Supply

```javascript
const CONFIG = {
  // ...
  maxSupply: null,  // No limit
};
```

### Higher Royalties

```javascript
const CONFIG = {
  // ...
  sellerFeeBasisPoints: 1000,  // 10% royalty
};
```

### More Attributes

```javascript
const CONFIG = {
  // ...
  attributes: [
    { trait_type: "Background", value: "Cosmic" },
    { trait_type: "Rarity", value: "Legendary" },
    { trait_type: "Power", value: "9000" },
    { trait_type: "Edition", value: "Genesis" },
  ],
};
```

### Different Image Format

```javascript
const CONFIG = {
  // ...
  imagePath: "./my-nft.jpg",  // or .gif, .webp
};
```

### Mint More Prints

Edit the script at the bottom:

```javascript
// Step 4: Mint some example prints
const numberOfPrints = 10;  // Change from 3 to 10
```

---

## 🔧 Common Commands

### Check Wallet Balance

```bash
# Devnet
solana balance -k ./wallet.json --url devnet

# Mainnet
solana balance -k ./mainnet-wallet.json --url mainnet-beta
```

### Request Devnet Airdrop

```bash
solana airdrop 2 $(solana address -k ./wallet.json) --url devnet
```

### View NFT in Explorer

```bash
# Replace <MINT_ADDRESS> with your mint address
# Devnet
open "https://explorer.solana.com/address/<MINT_ADDRESS>?cluster=devnet"

# Mainnet
open "https://explorer.solana.com/address/<MINT_ADDRESS>"
```

### Check Transaction

```bash
# Replace <SIGNATURE> with transaction signature
# Devnet
solana confirm <SIGNATURE> --url devnet

# Mainnet
solana confirm <SIGNATURE> --url mainnet-beta
```

---

## 🐛 Quick Troubleshooting

### "Insufficient funds"
```bash
# Check balance
solana balance -k ./wallet.json --url devnet

# Get more SOL (devnet)
solana airdrop 2 $(solana address -k ./wallet.json) --url devnet
```

### "Image file not found"
```bash
# Check if file exists
ls -la nft-image.png

# If not, add your image or download sample
curl -o nft-image.png https://placekitten.com/800/800
```

### "Cannot find module"
```bash
# Reinstall dependencies
npm install
```

### "Failed to upload to Arweave"
- Check internet connection
- Wait a moment and try again
- Ensure sufficient SOL in wallet

---

## 📊 Expected Output

```
============================================================
🚀 Metaplex Master Edition Creator & Print Minter
============================================================
Network: devnet
Wallet: ./wallet.json
Image: ./nft-image.png
============================================================

💰 Wallet loaded: 7xQ8...abc123
   Balance: 2.5000 SOL

📤 Uploading image to Arweave...
✅ Image uploaded: https://arweave.net/...

📤 Uploading metadata to Arweave...
✅ Metadata uploaded: https://arweave.net/...

🎨 Creating Master Edition NFT...
This may take a minute...
✅ Master Edition created!
   Mint address: GjK9...xyz789
   Metadata address: 3Hf7...def456
   Master Edition address: 9Pk2...ghi789
   Max supply: 100

💾 Master edition info saved to: ./master-edition-info.json

============================================================
🖨️  Minting Example Prints
============================================================

🖨️  Minting Print #1...
✅ Print minted successfully!
   Edition number: 1
   New mint: 4Qw8...jkl012
   Transaction: 2nX9...mno345
   View on explorer: https://explorer.solana.com/tx/...

[... more prints ...]

💾 Prints info saved to: ./prints-info.json

============================================================
✨ All done!
============================================================

🔗 View your Master Edition on explorer:
https://explorer.solana.com/address/GjK9...xyz789?cluster=devnet
```

---

## 🎓 Next Steps

1. **Test thoroughly on devnet** before mainnet
2. **Customize metadata** (name, description, attributes)
3. **Mint additional prints** using the exported functions
4. **List on marketplaces** (Magic Eden, Tensor, etc.)
5. **Build a minting UI** for your community

---

## 💡 Pro Tips

- **Keep `master-edition-info.json`** - you'll need it to mint more prints
- **Use a custom RPC** for better reliability on mainnet
- **Batch mint prints** if creating many at once
- **Test images** are properly sized (< 10MB recommended)
- **Set appropriate royalties** (5-10% is standard)

---

Ready to create your Master Edition? Start with devnet! 🚀
