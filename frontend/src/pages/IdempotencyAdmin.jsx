import React, {useEffect, useState} from 'react';

export default function IdempotencyAdmin() {
  const [keys, setKeys] = useState([]);
  const [selected, setSelected] = useState(null);
  const [days, setDays] = useState(30);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(() => {
    try { return Number(localStorage.getItem('idemp_page_size')) || 20; } catch (e) { return 20; }
  });
  const [loading, setLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(0);
  const [confirm, setConfirm] = useState(null);

  useEffect(() => { fetchList(page, pageSize); }, []);

  function fetchList(p = 0, size = pageSize) {
    setLoading(true);
    fetch(`/api/idempotency?days=${days}&page=${p}&size=${size}`).then(r => r.json()).then(res => {
      setKeys(res.content || []);
      setPage(res.page || 0);
      setPageSize(res.size || size);
      setTotalPages(res.totalPages || 0);
    }).catch(console.error).finally(() => setLoading(false));
  }

  function fetchKey(k) {
    setLoading(true);
    fetch(`/api/idempotency/${encodeURIComponent(k.keyValue)}`).then(r => r.json()).then(setSelected).catch(console.error).finally(() => setLoading(false));
  }

  function cleanup() {
    if (!window.confirm(`Delete idempotency keys older than ${days} days?`)) return;
    setLoading(true);
    fetch(`/api/idempotency/cleanup?days=${days}`, { method: 'DELETE' }).then(r => r.json()).then(() => fetchList()).catch(console.error).finally(() => setLoading(false));
  }

  function requestDeleteKey(k) {
    setConfirm({ title: 'Delete idempotency key', message: `Delete idempotency key ${k.keyValue}?`, key: k });
  }

  function deleteKey(k) {
    setConfirm(null);
    setLoading(true);
    fetch(`/api/idempotency/${encodeURIComponent(k.keyValue)}`, { method: 'DELETE' })
      .then(r => r.json()).then(() => fetchList()).catch(console.error).finally(() => setLoading(false));
  }

  function onPageSizeChange(v) {
    const n = Number(v) || 20;
    setPageSize(n);
    try { localStorage.setItem('idemp_page_size', String(n)); } catch (_) {}
  }

  const paged = keys; // server-paged

  return (
    <div style={{padding:20}}>
      <h2>Idempotency Keys</h2>
      <div style={{marginBottom:10}}>
        <label>Days filter: <input type="number" value={days} onChange={e => setDays(Number(e.target.value)||0)} style={{width:80}} /></label>
        <button onClick={() => fetchList(0, pageSize)} style={{marginLeft:10}} disabled={loading}>Refresh</button>
        <button onClick={cleanup} style={{marginLeft:10}} disabled={loading}>Cleanup</button>
        {loading && <span style={{marginLeft:12}}>Loading…</span>}
      </div>
      <div style={{display:'flex', gap:20}}>
        <div style={{flex:1}}>
          <div style={{marginBottom:8, display:'flex', alignItems:'center', gap:12}}>
            <label>Page size: <input type="number" value={pageSize} onChange={e => onPageSizeChange(e.target.value)} style={{width:80}} /></label>
            <div style={{fontSize:12,color:'#666'}}>Total pages: {totalPages}</div>
          </div>
          <table border="1" cellPadding={6} style={{width:'100%'}}>
            <thead><tr><th>Id</th><th>Key</th><th>User</th><th>Txn</th><th>Created</th><th>Actions</th></tr></thead>
            <tbody>
              {paged.map(k => (
                <tr key={k.id} style={{opacity: loading ? 0.8 : 1}}>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.id}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer', maxWidth: 300, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap'}} title={k.keyValue}>{k.keyValue}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.userId}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.transactionId}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{new Date(k.createdAt).toLocaleString()}</td>
                  <td><button onClick={() => requestDeleteKey(k)} disabled={loading}>Delete</button></td>
                </tr>
              ))}
              {paged.length === 0 && (
                <tr><td colSpan={6} style={{textAlign:'center', padding:20}}>{loading ? 'Loading...' : 'No keys found'}</td></tr>
              )}
            </tbody>
          </table>
          <div style={{marginTop:8, display:'flex', alignItems:'center', gap:8}}>
            <button onClick={() => fetchList(Math.max(0, page-1), pageSize)} disabled={page===0 || loading}>Prev</button>
            <span>Page {page+1} / {Math.max(1, totalPages)}</span>
            <button onClick={() => fetchList(page+1, pageSize)} disabled={loading || page+1 >= totalPages}>Next</button>
          </div>
        </div>
        <div style={{width:400}}>
          <h3>Selected</h3>
          {selected ? (
            <pre style={{whiteSpace:'pre-wrap'}}>{JSON.stringify(selected, null, 2)}</pre>
          ) : (
            <div>Click a row to view details</div>
          )}
        </div>
      </div>

      {/* Simple confirmation modal */}
      {confirm && (
        <div style={{position:'fixed', left:0, top:0, right:0, bottom:0, background:'rgba(0,0,0,0.3)', display:'flex', alignItems:'center', justifyContent:'center'}}>
          <div style={{background:'#fff', padding:20, borderRadius:6, minWidth:320}}>
            <h4>{confirm.title}</h4>
            <div style={{marginBottom:12}}>{confirm.message}</div>
            <div style={{display:'flex', justifyContent:'flex-end', gap:8}}>
              <button onClick={() => setConfirm(null)} disabled={loading}>Cancel</button>
              <button onClick={() => deleteKey(confirm.key)} disabled={loading}>Delete</button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
