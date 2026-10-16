import sys, struct, collections
paths = sys.argv[1:]
per = collections.defaultdict(lambda: [0,0,0]); fmts = collections.Counter(); total = 0
def utf(f):
    n = struct.unpack('>H', f.read(2))[0]; return f.read(n).decode('utf-8', 'replace')
for p in paths:
    with open(p, 'rb') as f:
        magic, count = struct.unpack('>ii', f.read(8))
        for i in range(count):
            cls = utf(f); fmt = utf(f); fp = utf(f); length, dlen = struct.unpack('>ii', f.read(8)); f.seek(dlen, 1)
            k = cls.rsplit('.',1)[-1]; per[k][0] += 1; per[k][1] += length; per[k][2] += dlen; fmts[fmt] += 1; total += dlen
print(len(paths), 'bundles, formats', dict(fmts), 'stored bytes total %.1f MB' % (total/1e6))
for k, (n, l, d) in sorted(per.items(), key=lambda e: -e[1][2])[:8]:
    print('%-40s %5d payloads  inflated %9d B  stored %8d B' % (k, n, l, d)) if len(paths) == 1 else print('%-40s %5d payloads  inflated %6.1f MB  stored %5.1f MB' % (k, n, l/1e6, d/1e6))
