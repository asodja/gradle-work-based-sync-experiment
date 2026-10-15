## m0-baseline

| # | Scenario | Runs | Total ms median (min–max) | Action ms | To projectsLoaded | projectsLoaded→buildFinished | Tasks | CC |
|---|---|---|---|---|---|---|---|---|
| 1 | Cold (CC off, IP off) | 3/3 | 34696 (34520–38185) | 34423 (34201–37906) | 19968 (19653–22504) | 14334 (14245–15167) | {executed=27, skipped=66, up-to-date=271} | - |
| 2 | Warm, plain (CC off, IP off) | 3/3 | 16895 (16691–18532) | 16871 (16665–18512) | 7269 (7115–7482) | 9450 (9393–10870) | {executed=27, skipped=66, up-to-date=271} | - |
| 3 | Warm, IP (CC + IP on) | 3/3 | 1445 (1055–2330) | 1425 (1024–2303) | 1360 (924–2222) | 80 (64–99) | {up-to-date=1} | Configuration cache entry reused; Reusing configuration cache |
| 4 | Incremental, IP (one script edited) | 3/3 | 14933 (14306–15963) | 14913 (14286–15936) | 4498 (4102–5995) | 10009 (9726–10228) | {executed=27, skipped=66, up-to-date=271} | Configuration cache entry stored |
| 5 | Warm, IP + model cache (builders only) | 3/3 | 1278 (1234–1864) | 1228 (1217–1844) | 1134 (1125–1778) | 83 (65–102) | {up-to-date=1} | Configuration cache entry reused; Reusing configuration cache |

