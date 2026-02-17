package com.creativedrewy.solananft.usecase

/**
 * Hardcoded sets of known-spam collection addresses and individual asset IDs.
 * NFTs matching any of these will be filtered out before caching.
 *
 * Add new addresses to the appropriate set as they are identified.
 */
object SpamCollections {

    val spamCollectionIds: Set<String> = setOf(
        "2MFaNdG4yR3UKzVCRF5ydsjuen35Zd21hqorzXJWxE53",  // 2.0 Jupiter AirDrop
        "2Q3eKwxxpU67iQapB5mi9KXcKcCJZ66CGYbjxtgjNdph",  // JUP Third round Drop
        "2vY5rXEALG4TdgMFScX6oLNS9jnNUhZrybEATWj7CuNc",  // JUPPI.io Super Drop
        "2wpjoaQAdr2v5y2txnbM3KRupsRvTtgShDPHiaPQqpjw",  // 2024 Jupiter AirDrop
        "35aH16ie2EQV2Xqu1NJbckbvkDkVmTEbGAxygQi4UxLr",  // 3000Jup For You
        "3BdtT2VXoLhZidrWFZNC1vBZ3wFkSF3B2GSzGUBrmYhU",  // Now Jupiter AirDrop
        "3j86EXTSZYyvP1xWj4CLbtGYDYZomv6iYzoxQfwyWhFU",  // 3000Jup For You
        "4YACsrLrFtW4jmGhUdMS8nbATPH9PyASrog24ar2wd4h",  // PIXWAY | Jupitar
        "5v6jK7a52agv3fwZJRPeWD1toNcVY63XS4yA9oGrx8HF",  // JUPS.io Token Box
        "5wcwAGhH4SkBqv2hJVJZ8t8ACCoksdvDcfVRi5DDxMZW",  // 2400JUP Drop Box
        "5XabRxWzAZHw27APMNW7FD13EckBCqECv5cSPT99uW9r",  // JUP Third round Drop
        "6gSgyH2xVtkBQiKtchZY4KhwREJBK3jGVYEwdArnpZd5",  // Jupdrop #4
        "76bXEQygpxrwR7FRSbjCgqncgjw8reJPy27VNZJMjG3V",  // 4000Jup For You
        "7Sc8LUZjaFGcJ2rR6fGjV4QpjsRSRqHVMSdHFf7MPioS",  // 2400JUP Reward box
        "7tMYvzict89gPc5ZxwSr47o7RFBkkJd1UU2XBTxVHv55",  // JUP Third round Drop
        "8oTWGCcosUSxoJVNPrpm6BtkD6z4dUZjxDNY4KCDF7UL",  // 2.0 Jupiter AirDrop
        "8Y1gaiWHKsQUk1wXbpZGntqGB4kcsUbffNdGgKu8TQu4",  // For You 6000Jup
        "8tCuWdWtpTiWBfibxp1VAifMfFmnSapgResduribLjsY",  // 4000Jup For You
        "9EZqapru6XkCyKcRp2RxHJzAGe5eHTw6c7JFq9X1JN9b",  // 2400JUP Lucky Box JUPCASE.io
        "9L2a5NLqtNbqqeybJHzikQAnhrTGDyaFbGr1ubNvQcg4",  // Jupdrop #3
        "9muvZmBF72TF47xMPsnsZ3gCzBENG3wqbWfNGheb9Wmn",  // 4000Jup For You
        "A5XyM3ue9xod9RWpDU2PFRgAxFUuFULZ2xvETLLAfFGM",  // 2400JUP Drop Box
        "AFvKMpRbGTQ2gJHFzw11cxRXKSD33ktNyWGyKvTTdHLe",  // JUP.BET PASS
        "AtE3hpXBBVJydzf3vfjNRKNXJXK6p3vrrRQkSZEoHdPe",  // 2400JUP Lucky Box JUPCASE.io
        "B14x59hpoVv7y2kN7wS2kmHQNhFcEWy4UVCPRZgFq3Ta",  // For You Jup
        "B6soqC15AM88FPeS153c9m4pqVDL7VHWJ8ZDXNePuzhH",  // For You Jup
        "BGRnYTwBqJEo3TGFrH5JtAXdGmWCwg3xp89QvjCFpKak",  // Now Jupiter AirDrop
        "Bh8QPTQRoBtAUBFFhFwjGQCaS8YCGt1x4xY53b6Vi7NM",  // 1900JUP Drop Box
        "BhKYoPVuP9CZepsbRU9jPXWc7oaxhYz91PDTT6HA4nVL",  // JUP Third round Drop
        "CG9gUZTWTuLfBRNfReAAKfYqobATUhEGLDm9xSKwXYmt",  // JUP.PRO Mystery Ticket
        "DrRoRFUusdY6MBAHypWPFci9roG9iURyZiWctdc1dgGN",  // JupPass
        "DVtaHeXG32zJiwudXaDH1s3R7h47Xk7YcW3dHVhacKuf",  // For You 6000Jup
        "E1998rbknK8tzXKHdHgXJYsnzsd2UF7YqMzPeEXcT6kW",  // JUPS.io Token Box
        "E4RFyZg8FMJ7973iQAASXD6ypKc6Cz1DCy1nzvBe7uY",  // 2.0 Jupiter AirDrop
        "EefKT5A9sZpRznEK6dVZBHJdkV7X7KwDLmqtrXN9T7Fe",  // JUP Third round Drop
        "EF4TtphJGTWvwnTNoryspWx3LmanGLivDC9rCQte5eTi",  // 2400JUP Lucky Box JUPBOX.io
        "F8zi6uD2QFYYDCNuZCeKX38ij5yp5iXbouyqx75eT71K",  // 2400JUP Lucky Box JUPCASE.io
        "FDErarL17P2gh2HW4JjRothswDrWPLKMZzqaEmebNPQD",  // 2400JUP Lucky Box JUPCASE.io
        "6f8Zt5zsLtVpvBrha7WRXiq4kRdB9wZAmcHLDET8qE1Q",  // 2400JUP Lucky Case JUPBOX.io
        "GJWdL9CKHUZ4WT26YENCHDeH93gXk49YSDvkBXQdGwXH",  // 3000Jup For You
        "GNWXAu3Y6RUJWnwdL82Fn5qP85HpPFXtDFnQdKhasruD",  // 2400JUP Drop Box
        "GpzCRZFgnHULR1aeBKJ5QZG67CUZgMHbRrfT1kHKrDwq",  // 3000Jup For You
        "GTU3oQuhZpU3zazW2HuCaNKzUpfzjkGMfidX8H7eZigx",  // JUP Third round Drop
        "HMxSfRb1mR8E4KoGtcKBPARNTikTpQmxsMkKKJ6ogEjA",  // JUP Third round Drop
        "8Pd8CK8zsyxn9qdmLM4J8n1E6NrkJxQfUC9wLkd4Jx9M",  // TO JUPITER (B&W)
        "2NxNp5JMNEk5A5QiKb3Lmk7PrM4KvpeoqvCq2wVaEsTo",  // Claim your 5000WIF
        "2NyYrkw863XswT8BnRJ8US8Y8H1gHt76BFk8d9Uhn1Rg",  // Claim your 4000WIF
        "4WeirYE5eCDVUk2C7bGwcazMiN9dp2XwhLyV39p9ALgc",  // WIF Drop 3000WIF.com
        "8cjCxkWrZsDk6yirbUPHMMzto51RuT3HNTj3CPvJMD8c",  // Claim your 5000WIF
        "A1L8Ea7yn68c88CwyRtjmzLpAmdECNdgeCqvA7JfRkiJ",  // 3000$ WIF Lucky Drop
        "Ar1atc7jXd4fN32LGgoTCDRh5LzQjNjjvH1sDW16BNxk",  // Claim your 4000WIF
        "D9VdchX5jmvRDfvw39HT91wxuk42fv4uDD34XGAEhFdY",  // Claim your 5000WIF
        "DQS1cysazMHqUzc5U5J5cYgQNekQGR6Rg7mdDY4nzHPS",  // Claim your 5000WIF
        "Dwj4vfufY4WzaB6xVHKJFqqKbUMmZPxDuCFRT7SfvhHG",  // 3000$ WIF Drop
        "FfQbfERFNMejqoAAMJ7YhraJcbhcCsJn8F42wadVVRtk",  // Claim your 4000WIF
        "G1tx2qAv3UhHMa5NCoH3Wf52UHQGH7W38BmGBKwtdnGi",  // Claim your WIF
        "GdeidFAXVaKfJGXhtbYy24iaoSgoZtyS37LKrfx8cu9f",  // dogwifhat scam
        "Mg78GVDmHLobbspfBihsUYnuesdoiQv8GtDX3Qd6VGQ",  // 3000$ WIF Drop
        "3nsezps1obhcopc6trvyjMhcyTUVZAWDG9Mn5vE8UjSy",  // BOME Drop
        "5gD9z7byWpQnKPRWWXWrZZ5SVKiZtGbTSyciadsMfLFN",  // 1000$ BOME Drop
        "5SyWxzB8zZ5gwCQDPd1udojcEEbHreRpvuw2f8AsU1am",  // 1000$ BOME Drop
        "632LXCqwLQeodaDFfUTvEoBFyEHGw7ewQq3iXYHWtN2r",  // Claim Me BOME
        "65XHvySAkYA21RrEWoWcxRKuw368dq6Li5ZaXsZELvm3",  // Claim Me BOME
        "8B59xULyfT4cpmZwpJ58EGLFx4aKYe8yKpZMJAeF1kAs",  // Claim YOU BOME
        "9Y2YJF6BjcvQ8otJssMWF4ztVFCzq6LiBff7AgrZUPTS",  // Claim Me BOME
        "9nsHv8vKBmvDRjQ3gsTp9EY1aaiwUXk1gMaXqhjMNBmK",  // Claim Me BOME
        "BiMJdQsNK19LuXDMjwzQUpiPjzPV1bJwxhngm7FrTZVh",  // Claim You BOME
        "BXHQABXfdL44HMJjnQnfy1iECmvwu2NsTYTqooyexs3j",  // Claim Me BOME
        "DTdYUf1ZkSjdLACp9wNnrRrR92nyUCwPYjk8XuPLepCV",  // Claim Me BOME
        "FtzSLdpvBeX4PJ1VgCa5BDQ1r8kACkSxh7cmvg3Fv9yv",  // Claim Me BOME
        "HgEupUZTa1JkXqmFwoSTXVQDeCaaz6ZJNoxXSbhjE7MB",  // Claim YOU BOME
        "HV6m8DiNhTnUm23uDN8Uja3cdeG4r3CdqvmFDevk8Ehb",  // Claim your BOME
        "59VRE8WfoVZvNqpuZmAiZYost5nEFhAA7owBsy2qZ68B",  // 4000$ W Drop
        "D3GRpSFViCMbaSCRLpg85ejSGeVn6TThMT8bQpAGZYpn",  // 4000 W Drop
        "Hjf5VZZfw1YWQP7dDHYA6xDoHUZxz56oKevqyEBFYkyw",  // 3000$ W Drop
        "JC79ovKXxFE1YBWCoXpnrsshyyat5jS1BHvnMnQgSgb1",  // 4000$ W Drop
        "28G1z25Gttfovb6AdoGCPA4eSUwv2jaWtFQkK8KL9xK5",  // PUMP.FUN AIRDROP
        "4BS1NVQkvjcULxhRj66qdJVksnsWjRFx6u6vQQiXU2ux",  // Tensor x Dori Samurai AirDrop
        "5c3BUaoj7GNBLyRFUoRNNnt5kqaKTc68DNrCYAQijb8P",  // SAGA.PROMO AIRDROP PASS
        "7zxcxyqzVdACgpLdkt6nmG6w92RcwoK4CtJrvHScHkow",  // $ME AIRDROP
        "C8ctVpPK2x6e6ctfXzj11QcJeZdGLYB9AYSyV16LYS2G",  // Bybit X Solana AirDrop
        "DNdweAm8ANfR7zwosE6grjsMAYQjaWiK9WGu3UcX3bG5",  // Bybit X Solana AirDrop
        "E4Yr926kTYYxfZWMtdLBdTZmS7UjF64JVoGRqxXXab5s",  // AirDrop
        "EfuLCUpQpZHvcyNwho7wUbsbjT383GvYqahTAfJiA2Px",  // SAGA.PROMO AIRDROP by Saga
        "FLRxZJb7Kpd5i9Q7WdH7r5uRqDL7oJVpqW3ew8FpE336",  // Happy Airdrop Season
        "G2vhnYnTYyGZW14F6PnZHehXzuP7i66CpWzpurtGJe8m",  // Solanaconda Saga Gen NFT Airdrop
        "BXvpQMSNwCXf6DdC7JBTZx2iAUbFB2Mkoe7qu9ApgixY",  // MFI.EXPERT PASS
        "EFEJdUc4ZywgceqsCZTpcuxVbQ23KKvw9MmqygiHpKPz",  // TENSOR.MARKETS PASS
        "GzXmLd3DgqRyUcGur6NuxYKbK1oRhUWNbmi2nsWGnXpB",  // MAGICEDEN.LOL REWARD
        "23URm1wtJa95iY9dgQJ6z6MmDh2EMWFb8jcfMn6bFYjq",  // Tensorians Week
        "AB4k6f7znrh9VxMNWsd1vTUgvXSY7As3bjU51eGiMvnQ",  // Enter the Tensorverse
        "FYvomrQxRxTNuiteZwSEWNvGcCGJVM6vopudYNUp6nWh",  // Track NFT sales on Tensor
        "3b5vW2Z7EAC7n11vx2Zdmi1WghoSBy936PW3cAVyvb2u",  // $MANEKI CLAIM
        "4d7ytHR5Nr2jpxVYQStuqiEhpnJduDYhnjQ4n3CiQkWi",  // Active Staking Rewards
        "9LAUEafDYrHnvXAWUF9CEv1GySckz18sgMWaxrptdB36",  // My gift to you
        "CoLsuqvJ9YbPkNVmGq3J4tXds6PguztnYD52qmTPW2c",  // Redeem NFT Voucher
        "CscaaPX5HDGY8KAcNddUVa77mWdpBwmsZoafYDJ3vVhK",  // 1800$ Lucky Ticket TAKESAGA.com
        "2SzeQNXfmyUgQbbbrwedGSpSsHFhF1SyNYB5i6nUouaj",
        "2YnTugjJXsoRVRtZPyqDRBRT1XBygyUpov1vLwDTsDuN",
        "2i5xrYoYsLppWTsW8C8HsPz5G5GfKtxGSebgAAeKrsAL",
        "2xJd6CcQ4bXKwo8R1vGWW3NV7rzgasUqauKNyrLddZw3",
        "3EJr3Lya26q5njaZaQ2ZcHkpmHPhDHt3MrGeQbCjzZYH",  // Exchange Art Loot Box
        "3jZRbRD6mayuDZiDmEDsTBta56ue8vGSFZgjdgKrF2MT",  // Sponsored Droplet Common
        "443sj3LjNafbMx64JTcY7Jhb7hU5H5krLiDueukckeH8",
        "4GW29XZQTtk4iKttRdUF1MLEc6gRYhYH8BPd6jfLSGp4",
        "4NESpqZiyboAsXs3hZztnBd8145kw89DfWsFo4MPpMN2",
        "4TczbPjYNXpGu8bD1UdrbWMWypBmK5ikiUS56pArTNo8",
        "4ru7khZwe9sui5sRzWv2GeFjjrsDDvqvTTVNm6BgbcTa",
        "54NWwPJH6FRGaie9DWk7vGvtzcrmNC7H7XCTy5HwQnKh",
        "596USuYxgVyg4zBL5zhYMHqjcRgn4j4M6i9j75Togva2",
        "5mnpY9uxmyeML8peUMh1pbrEAgRn4oLkGEyC2tXk1zgT",
        "5ouJwoUseZc5rGZnhD3uF8kTcXMdBoY2z8q3H2hc34W8",
        "5rFwEG2GLKqUeHsomcUXSPbbBX6t3jby1tbzq7WYecxa",
        "65X844SbLL5YYCE87VHCcdiedMjtSjfPPYWf9bLtkpN",
        "6F1K6bBrB6L1SMsJ3oLX9PqH8CyGmjhTee2aThCgMcDo",
        "6FN4AseBtaSseXKNHfuQgVpGevcK6qvcX4Vg3CU2USym",
        "6LD48KppHAGK2iHpR1N8jM6XMSXJuLmr5g1BRWWQMWN1",
        "6PQ7MZ2Ei3rik8qReok37nqsmxFrRduP7kRRkdjYDdS8",
        "6SPCAThC5zvQvQqsmVdFXgr85tKGcVoCtjZ3Dq3V7ucK",  // Solana Game Pass
        "6XGjRV9VbUSdNpj7ZW47Lzxvx1ibuW3EVwHnoTVYCh7v",
        "6p1kn2q5DRtYAtn8PkR1cdx4F8D7TUp4dNjLYeFPt9uA",
        "7cYYsBtyhNhugFi4DVbZDy1uLAVNvDn6X38MemHripWE",
        "7jRVfjUQEfPiRLtYNRvY3vAP8ALpq6CLZ9nwQKwSpPLD",
        "7NshXyVpCfC6mBLbMwzpcE85S2ueQZWFtUdmU29nNP9m",
        "7RE7Hgf58etMSSMWhj1A9CpghUZVhDgrpyGhd2HnycFE",
        "7xqZS221QdJtuhL5uwzxZJE7h6ZnakPMxheNH1okWqrC",
        "8DDZz6u6PtFpKfVJBWqnEZLyfx9MPTAtnZSzVnAuu5Y2",
        "8EZhRWgWwZ4LztvNjF2PFaYcDmxEAKCHJbZazHyToGqo",
        "8j7uMkbJRcinG2kQsYsVyPWC226hPHBw4kYmrckVHD3S",
        "8NqTiTWeGNMtRTKPMjgWWqu8heqW7X8zezVSsmyh6Bw5",
        "8r7B6CTpeTcWv8ZfzaZUQtCAmNkjiojbCVjA5aZSmSgs",
        "8Us8LERRTuiaZtPKxFn1UWBzD5uVgtsXyX9D19yn1dJc",
        "9AqWTMA8JE5QE2RvacLk425nfkJHuW32XkQFiY7KWaVr",
        "9TFUf5fvhcJcBVFF3VJBiMuYTz4nJjNTcuTSadSKujJm",
        "9TNoxXAFNN5kHpmFhnPk2kdjCd3bM9vGmuKudqswLWhG",
        "AHhUyZfFpv3e6eqZ4BQBW2hx6kX3365HJVWCrUSXEkhp",
        "AhHn9PRttGF8XAi7onB2goqFYCmVbbRV6Quf3hjVUqwf",
        "AKbM6UaEwe3PsriWbeYWNYmvEQmvaKNJ2GH2YwcdEcFE",
        "ASHNoZ58Kqe46N4XDjVUEBDSLXVtcz292r4ZuZBt6rF1",
        "B2tfrT5t71PJrjniaeuqob9DZCKu7ubA4gRf2wYFB9AJ",
        "BDLTFarhHKonNuuDgrdAriHzUpTCSvLtCELG81vuteJe",
        "BG5onh8q42NhNQ68DuLhzKwYsNQQinRGnBJrAJfYCpoE",
        "BHUJwqcniZtPR4EU8XfgBp4JPCLwworGrVcEKLVX9wDd",
        "BLN17MF94QYW5nWqPj7bkSaaQUGEEgQmXwACK1qYzoHH",
        "BwcdNV6gWZG57ZHeTn52MZj3zAkrYD4yWurAs3Q6Ju4j",
        "BYnmpDzjFaEs6dghi5UsGTL5ZyoAPbwWfMaVFzDcVj8",
        "BZ3DohF6BHGkAnZAe1g8ohWVuh95bXT4FhiGw1BXJWfF",
        "Beh2JEo3MgXwJSEnNe9qSEZWL7LLuYokGcewsfaNkawu",
        "CFur8MYb6t4d39dnKBJjWitds1wyUtVyzVLstu8d34wW",
        "CGkNXfWS6SfL5MwTvcJHqZ6oasPfyhnNwnjL4eBib3VV",
        "CoLLiKtU3YV9HexsxXGFHjoLpCF8vPdivjTtvLCBhNn",  // Collect Box
        "CzFrhR4SDbkaYGADgPV9AnJma9pk5WaHhbfT55gVHP6z",
        "DdLF118Xxma3CsJoXVxCR9VK2PWa5aTJ2V12B4c9Sb5R",
        "DHJEQWxPojY2y4LppCdaVUUqWQdNtrwvACXPXDNXPGM3",
        "DPNTcMcvRs4XJVgKzKBqS3Tg6tRbEWyqJ6jgef5HkBxC",
        "DY7WB2qYpvBguDhWdqBqVzZGw9AqSZCp2e2CQ7iowaMe",
        "DzxVz7QWyRQtL5p33RqxMJ3NC2dKAAh8ZUyNmwK5DRDJ",
        "E19WWBJmJxEyWurTSLUKqSndXx8LBtnBo2oYmChwvM1a",
        "E6RYbbnWMatxyRawvTCKfPgdxXcpL3eWM82pYyMv7beE",
        "EHRscess6jotQs1dLJr5Ws4Ti9cS6SQTPiDcUxbVka7f",
        "EjpKPUA9DQVNRjkLuuLV2GQWv9ZRoZUFGFDqsa1sF4Ja",
        "Em9wb7niKr9bkvDeYnzA57XPU11KDTBSKBnY7J4SHPFL",  // Drop Pass
        "EMCQ1fC1hWZqmvbwu49LyBFcAyGMBC7pKddFS6R8kqhx",
        "EQaTVF4zgNHqHvYqo4fDgce9uqmHk24GKUrYoUb9F6QB",
        "EQfAgabfbMkJzV5g8dRbnJFFxU1uANPg56JoCrsPjCru",  // Rare Pass
        "ExLMcVksoikgW9F8Ya6eLioP1jhcTmoiT1EMVfE7GqYG",
        "EXXAJANLCogLHLgwWQCJoz766HXzc9YDsQj2fdMhMsBK",
        "FFX3jT7S4JgsFMC2t4DT2dsumyMVUwHy6MYBqr9bVPzM",
        "FimbGvCGYksWVXMmkufEkY8ApbmLAGX6fBhdwz5wrUaM",
        "FKEr2dpYMpAkvFvQz3AWU9sLzMKVHceyjafcUTLaCe3K",
        "Fms4RmMNCS8XkrcaTRGkFRjkWzqNt4YesoXVnwFxJ2QB",
        "FQNi7zqxKJdUWiN1CHYfJAwPZe3XX4GGZ7SmRi23tdca",
        "FSdotujgzdodpYMtWmzT6G62NAgtTdruKT9MekbqCaB8",
        "FSfnbmq3b15PbpiPcrJDaz4xRCYTbWzAMUY7NhHRinSF",
        "FtKNsZDQ8dePQz55DCXrp88ywBg3gnCGC7tZEgf5gvR3",
        "FXaHEC8ADT97EFLaLfVCECeSsrQ2z9L5PV2XBGDtKTmV",
        "G37CtZ2HtN6eCSt9swcXxmRyovcwaC2cSv5ENQFmMeGY",
        "G3RJMoaGWcHGKrHzxsm4gB5YPUW5pqeHHX3QgQ3kd3EC",  // Primes Airdrop Box
        "GAc7tC8RDBkcbzHKqXjCAVUaGzwkzWFD24Kje6zWoyYh",
        "Gk8ZtnLNTTjzngZTUKxfwwWXYvNyFLJjoAznfmCwB1HP",
        "GmmLBVzDufqGkC9sCX8hvceyCRdKEfecQ9ZBe52sP3J3",
        "GnztCrbsji3qWrwkRgVkD4EJPdHz8iq1Jq7KHHVK96T1",
        "Hdicnved7dyPUyMagmzPwKJNYi632cZxpamobbh8xqcs",  // Drop 21 Process Video
        "Hj6evx1BJq66eGJrBUMBRXkYBUZ3wgkFyaxJrdcfYYSS",
        "HKN1niBGMYFki73LUfh6QCDY1zBvFpNkSHk3WsJ1xS8N",
        "Hs7vHnXnVD2JUjLKsdXmM2JPEJTpzpvFrxxhCLCjioqQ",
        "HviMp2T3kFRyTtt8k69R67S6eCiENAb2ZEXtfQSVyWy",
        "HW8cYdDb7hjmp42XEgmLyt8k2HBicgXpKqNXAAZgDCmB",
        "HZZtebTAFgHotrWn6zJXxfa9zRNiZtB7sNMyZYVpLi9i",
        "HZyHKELUDJUkkep5JrxtN4bEVQ3QaisCyydD796wujx8",
        "VoTrkf7cmUWxqKnoFYsQA4bCZZuXPuMQ3GDKKrBAw7T",  // Mythical Box
        "aAgGv3rip5pDmuPEZqHkQLZNuLnwgYDuL44uX8Lyeyb",
        "aWQLmGorC6yxSAUP9bwRe4ptASYQLVbhczUMHdmKPA7",  // Rare Pass
        "nHM6RbMCNmWWFWNLP8TU6Jt4VnWVqqc4ejJTWLiJ4zf",
        "yzh4wJgxjNatoGNwfd4syBngXGTd22NiEYmpdmwyTyf",
        "7Nbk8nKHXisM9busRg3aXHWmZ7J2uPTKBWcXieNJt6on",  // One Million NFTs
        "HoRVCYviJtufgaRXRki9m27YatbJWZPzena9x9SgjWd9",  // INVESTIGATION
        "D2uBwTcivDHBLJWtRWvAeL73eypinZ7ET8o2qS6PtimP",  // INVESTIGATION WALLET
        "57VbPgsLexAFoxvyj9YRAVbMwkzML9ZXfdx64wnkaiXA",  // SAGA.PROMO PASS
        "BjzwR9Zibz55zcuCafrPt9gNnE6C5MrPozw4Jtsaagvf",  // 1700$ Random Pass TAKESAGA.com
        "D1GtuhEWFgtpH4Vuh11Jgs43MqNG9djcQYkPPH2wF5Qb",  // 1600$ Random Ticket TAKESAGA.com
        "5UEoNvysMDgrQG2D5b2ya9fiGhCc1nNEMRFGLzDZgi7i",  // BACKPACK.PICS PASS
        "9VkQvgB6Htpei8zdq6qEpQ5WuAgt1k8tmicY5yK6VFb",  // RAYD.PROMO Luxury Pass
        "J1EbCEC9qKjbt5S2XMJN4id6udyNeT4HRhQ3aLQK6b9Y",  // RAYD.PROMO Pass
        "EpEwxZyqqdbnZaudm8Qpctf6BaUKS4gQ6NC2mc2qeNzQ",  // MFI.EXPERT PASS
        "F9xmyn14Q5mkNPn36xWXKFs43Kvi4s5uznpmDHYEbA7Q",  // MFI.EXPERT WhiteList
        "2BEAtXJZGncSdX41pYEB5XcfvCGC7sgDQ8M2SbXma1Ah",  // TENSOR.MARKETS PASS
        "HkQyU5aUadAbJh427hG62UbyxLG18hnEKuZSFcmH81Ri",  // USDC NOTICE
        "coLvgLuyrc6eg7vNXChJTKHD1J68odwSYAq4G9XovQE",  // Phenomenal container
        "6Yw2PXmCTsXqdX2HxHsBwSMEaag7nSjQjWpCdAu5sbj2",
        "D1GtuhEWFgtpH4Vuh11Jgs43MqNG9djcQYkPPH2wF5Qb",
        "BjzwR9Zibz55zcuCafrPt9gNnE6C5MrPozw4Jtsaagvf",
        "FmWEjkvAVrCKU1Kq8qJ7zAqAnohyVAvB3crutEaBuE3f",
        "5UEoNvysMDgrQG2D5b2ya9fiGhCc1nNEMRFGLzDZgi7i",
        "CGQTUR1yGaXsPc5Z75n6jNsJePhoVDCvRQ8UiQ8vPuYx",
        "D2uBwTcivDHBLJWtRWvAeL73eypinZ7ET8o2qS6PtimP",
        "HoRVCYviJtufgaRXRki9m27YatbJWZPzena9x9SgjWd9",
        "EpEwxZyqqdbnZaudm8Qpctf6BaUKS4gQ6NC2mc2qeNzQ",
        "F9xmyn14Q5mkNPn36xWXKFs43Kvi4s5uznpmDHYEbA7Q",
        "9VkQvgB6Htpei8zdq6qEpQ5WuAgt1k8tmicY5yK6VFb",
        "J1EbCEC9qKjbt5S2XMJN4id6udyNeT4HRhQ3aLQK6b9Y",
        "2BEAtXJZGncSdX41pYEB5XcfvCGC7sgDQ8M2SbXma1Ah",
        "2dBm5reBf5sX5mhuFawotta9zB13jVBbsvuLfCQJ9tTX",
        "HkQyU5aUadAbJh427hG62UbyxLG18hnEKuZSFcmH81Ri",
        "57VbPgsLexAFoxvyj9YRAVbMwkzML9ZXfdx64wnkaiXA",
        "57VbPgsLexAFoxvyj9YRAVbMwkzML9ZXfdx64wnkaiXA",
        "EfuLCUpQpZHvcyNwho7wUbsbjT383GvYqahTAfJiA2Px",
        "5c3BUaoj7GNBLyRFUoRNNnt5kqaKTc68DNrCYAQijb8P",
        "5swkApGCpYA7c9D5u3pgDdahryatzHmtUZfZH8j7Xk7c"
    )

    /**
     * Individual asset IDs for spam NFTs that have no collection grouping.
     */
    val spamAssetIds: Set<String> = setOf(
        "CffxBxxyTBCk3htQKS7o7y8s98fMbkJtdJwZoXcmJj4e",  // $TNSR REWARDS
        "2fqgGuUXUp6SSKX3SzySLnpkQ6U4jZJ5ZFQ13NygZwUk",  // $ SOLGIFT7.com
        "2giDxywJRaSsmoh9YAwbkSQqsyk2H8sxaTygAx6NwgMw",  // Claim $10549 at SOL380.com
        "3KtDPJZgMzbKD6wRn83KykA38okMvNSbhdRD3qJfBXGj",  // $1000
        "5FT74RSjjHADt9kfDnJCshHmx52yinisG8VdjYJDH2KC",  // Claim $10653 at SOLCrate.com
        "9BaT6nYxXq9c1mL5Bc1792k5LhvJ3ABbm6ux33VbtqJi",  // Cyberfrogs x Saga drop
        "GLbyRF5q6zUAtYnvtN53inpNU65k2xJf8C94PGRnUBtS",  // Sаnctum #2025
        "FvRX7pNWWAm5WpW125Baszm26aMrksEbp5HYUcYRoHbk",  // Sаnctum #86
        "EbggfqkySCtrjsN8EWAe85HQG3nDf7sCjBYWHinVjiLK",  // Clоuds #2837
        "CBGgY9m9GXWua7aiVF1MiMEmYsF1HqVEMYiFqotjwpVL",  // Clоuds #2136
        "4va9LdmwLHfREtMkwzD5tVVfmCHRjdH79gDf1PpSRnnA",  // Clоuds #1577
        "95WDfdxaGBzD4g22Ncu6MmJu7G9PbHVoYvZ3qGNNFrAq",  // Clоud #2360
        "8zpjK4aEybzZzkXBDStMgM6n7qbce1o3FANb6652din4",  // Clоud #1429
        "4GBetqQSYXwNEd5T1uvGckbpgyTvAMpryVpwjt6pDn6X",  // Clоud #713
        "CLTtQpMXWTUircmRLnfSeaurvHNSQ1uJFDPGTKCATeRP",  // Slеrf #1394
        "8uofPtc3Deqzd77etuv59858pHaR3EAnt4G1sP8UAg8t",  // Slеrf #663
        "5XLfmBxMA2jD4e4aHizfw7VyHC8ozQmWY55XGXnnLGnc",  // Slеrf #954
        "6WqKyevypLdeEAgrcGiLmaai8WsovCC96KhjALhz4Lqr",  // Slеrf #1198
        "F868keBdR9RV5xWhGUyoPaCA1LDyEwtGyXY5utDcjoh",  // Slеrf #2286
        "BMb7zwNByP4EHbsV1xxPHVBfgkRiLGzzjqsMcgk1yQ9T",  // Bоnkеr #2519
        "9DKQJdcFmaAJuLhv4m7xCgdypjjaWmeZ6GgyS7orP32d",  // Bоnkеr #2888
        "87RQLdEGecGvjMj6FZVYEpQkdr32ChRZFMeP3Wv7MJuc",  // Bоnkеr #1498
        "24VZASi5hZQzFgUiVD1AxxREdWWgjerwTWySpYpepTrM",  // Bоnkеr #206
        "6upC2bAeWxdQThSRx1pc33u9KcW8NcoUwP2JHJokY5Mb",  // MEVSOL.COM
        "3VTvHWvC7X739xpuasbrbadHGMdgrDC57iNdkMBkSCwc",  // MEVSOL.COM
    )

    /**
     * Returns true if the NFT is known spam, either by collection address or individual asset ID.
     */
    fun isSpam(collectionAddress: String?, assetId: String? = null): Boolean {
        if (collectionAddress != null && collectionAddress in spamCollectionIds) return true
        if (assetId != null && assetId in spamAssetIds) return true
        return false
    }
}
