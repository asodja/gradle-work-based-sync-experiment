import json, re, sys, zlib, struct
def ion_text_to_json(path):
    lines = open(path, encoding='utf-8').read().split('\n')
    # skip the symbol table header: find the first line that is exactly '{' after '}' of the header
    i = 0
    depth = 0; started = False
    out = []
    for ln in lines:
        s = ln.strip()
        if not started:
            if s.startswith('$ion_symbol_table'):
                depth = 1; continue
            if depth > 0:
                depth += s.count('{') - s.count('}')
                if depth <= 0:
                    depth = 0
                continue
            if s == '{':
                started = True
            else:
                continue
        ln = re.sub(r'^(\s*)([A-Za-z_]\w*):', r'\1"\2":', ln)
        ln = re.sub(r'null\.\w+', 'null', ln)
        out.append(ln)
    text = '\n'.join(out)
    text = re.sub(r',(\s*[\]}])', r'\1', text)
    return json.loads(text)

def dfl(b, lvl=1):
    return len(zlib.compress(b, lvl))

def varint_len(n):
    l = 1
    while n >= 128: n >>= 7; l += 1
    return l

class Packer:
    """size estimate of a binary form: string table (utf8 + varint len) + varint refs"""
    def __init__(self): self.strings = {}; self.body = bytearray()
    def s(self, v):
        if v is None: self.body += b'\x00'; return
        idx = self.strings.setdefault(v, len(self.strings) + 1)
        self.vi(idx)
    def vi(self, n):
        while n >= 128: self.body.append((n & 127) | 128); n >>= 7
        self.body.append(n)
    def bytes(self):
        tab = bytearray()
        for k in self.strings:
            e = k.encode(); tab += struct.pack('>H', len(e)) + e
        return bytes(tab + self.body)

def ssdm(path):
    m = ion_text_to_json(path)
    raw = open(path.replace('.txt', '.ion'), 'rb').read()
    print('GradleSourceSetDependencyModel: ion binary', len(raw), 'deflate1', dfl(raw), 'deflate6', dfl(raw, 6))
    # artifact table: key = everything that is not per-usage
    table = {}; refs = []; n = 0
    def art_key(d):
        t = d['_type']; i = d.get('id') or {}
        base = (t, i.get('group'), i.get('name'), i.get('version'), i.get('packaging'), i.get('classifier'))
        if t == 'ExternalLibraryDependency':
            return base + (d.get('file'), d.get('source'), d.get('javadoc'))
        if t == 'ExternalProjectDependency':
            return base + (d.get('projectPath'), d.get('configurationName'), tuple(d.get('projectDependencyArtifacts') or []), tuple(d.get('projectDependencyArtifactsSources') or []))
        return base + (tuple(d.get('files') or []), d.get('excludedFromIndexing'))
    by_id = {}; back = [0]
    def walk(d):
        nonlocal n
        if '_type' not in d:
            back[0] += 1
            return by_id[d['objectID']]
        n += 1
        k = art_key(d)
        idx = table.setdefault(k, len(table))
        r = [idx, d.get('scope'), d.get('classpathOrder'), d.get('exported'), d.get('selectionReason'), [walk(x) for x in d.get('dependencies') or []]]
        by_id[d['objectID']] = r
        return r
    out = {}
    for e in m['dependencies']:
        out[e['key']] = [walk(d) for d in e['value']]
    print('  records', n, 'back references', back[0], 'distinct artifacts', len(table), 'source sets', len(out))
    js = json.dumps({'artifacts': [list(k) for k in table], 'sourceSets': out}, separators=(',', ':')).encode()
    print('  compact JSON', len(js), 'deflate1', dfl(js), 'deflate6', dfl(js, 6))
    p = Packer()
    p.vi(len(table))
    for k in table:
        for f in k:
            if isinstance(f, tuple):
                p.vi(len(f)); [p.s(x) for x in f]
            elif isinstance(f, bool) or f is None and False: p.vi(1 if f else 0)
            else: p.s(f if f is None or isinstance(f, str) else str(f))
    def pw(r):
        p.vi(r[0]); p.s(r[1]); p.vi(r[2] or 0); p.vi(1 if r[3] else 0); p.s(r[4]); p.vi(len(r[5])); [pw(x) for x in r[5]]
    p.vi(len(out))
    for k, v in out.items():
        p.s(k); p.vi(len(v)); [pw(x) for x in v]
    b = p.bytes()
    print('  compact binary', len(b), 'deflate1', dfl(b), 'deflate6', dfl(b, 6))
    # how much is the table alone
    tj = json.dumps([list(k) for k in table], separators=(',', ':')).encode()
    print('  table alone JSON', len(tj), 'deflate1', dfl(tj))
    # JSON of the original shape (no table), to separate "format" from "table"
    oj = json.dumps(m, separators=(',', ':')).encode()
    print('  plain JSON of the same shape', len(oj), 'deflate1', dfl(oj), 'deflate6', dfl(oj, 6))

def pdeps(path):
    m = ion_text_to_json(path)
    raw = open(path.replace('.txt', '.ion'), 'rb').read()
    print('ProjectDependencies: ion binary', len(raw), 'deflate1', dfl(raw), 'deflate6', dfl(raw, 6))
    # component table keyed by id (the resolution component id); nodes are (compId, resolutionState, selectionReason, children)
    comps = {}; nodes = 0; refs = 0
    def comp_key(d):
        t = d.get('_type')
        if t == 'ArtifactDependencyNode': return (t, d.get('group'), d.get('module'), d.get('version'))
        if t == 'ProjectDependencyNode': return (t, d.get('projectName'), d.get('projectPath'))
        if t == 'DependencyScopeNode': return (t, d.get('scope'), d.get('displayName'), d.get('description'))
        return (t,)
    seen = {}
    def walk(d):
        nonlocal nodes, refs
        if '_type' not in d:  # back reference by objectID
            refs += 1
            return ['@', d['objectID']]
        nodes += 1
        if d['_type'] == 'ReferenceNode':
            return ['R', d['id']]
        comps.setdefault(d['id'], comp_key(d))
        return [d['id'], d.get('resolutionState'), d.get('selectionReason'), [walk(x) for x in d.get('dependencies') or []]]
    out = []
    for ss in m['componentsDependencies']:
        out.append([ss['name'], walk(ss['compile']), walk(ss['runtime'])])
    print('  nodes', nodes, 'back references', refs, 'distinct components', len(comps))
    js = json.dumps({'components': {str(k): list(v) for k, v in comps.items()}, 'sourceSets': out}, separators=(',', ':')).encode()
    print('  compact JSON', len(js), 'deflate1', dfl(js), 'deflate6', dfl(js, 6))
    oj = json.dumps(m, separators=(',', ':')).encode()
    print('  plain JSON of the same shape', len(oj), 'deflate1', dfl(oj), 'deflate6', dfl(oj, 6))

ssdm(sys.argv[1]); pdeps(sys.argv[2])
