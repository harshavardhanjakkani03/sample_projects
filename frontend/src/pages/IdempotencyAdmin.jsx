import React, {useEffect, useState} from 'react';

export default function IdempotencyAdmin() {
  const [keys, setKeys] = useState([]);
  const [selected, setSelected] = useState(null);
  const [days, setDays] = useState(30);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  useEffect(() => { fetchList(page, pageSize); }, []);

  function fetchList(p = 0, size = pageSize) {
    fetch(`/api/idempotency?days=${days}&page=${p}&size=${size}`).then(r => r.json()).then(res => {
      setKeys(res.content || []);
      setPage(res.page || 0);
      setPageSize(res.size || size);
    }).catch(console.error);
  }

  function fetchKey(k) {
    fetch(`/api/idempotency/${encodeURIComponent(k.keyValue)}`).then(r => r.json()).then(setSelected).catch(console.error);
  }

  function cleanup() {
    fetch(`/api/idempotency/cleanup?days=${days}`, { method: 'DELETE' }).then(r => r.json()).then(() => fetchList()).catch(console.error);
  }

  function deleteKey(k) {
    if (!confirm(`Delete idempotency key ${k.keyValue}?`)) return;
    fetch(`/api/idempotency/${encodeURIComponent(k.keyValue)}`, { method: 'DELETE' })
      .then(r => r.json()).then(() => fetchList()).catch(console.error);
  }

  const paged = keys; // server-paged

  return (
    <div style={{padding:20}}>
      <h2>Idempotency Keys</h2>
      <div style={{marginBottom:10}}>
        <label>Days filter: <input type="number" value={days} onChange={e => setDays(e.target.value)} /></label>
        <button onClick={fetchList} style={{marginLeft:10}}>Refresh</button>
        <button onClick={cleanup} style={{marginLeft:10}}>Cleanup</button>
      </div>
      <div style={{display:'flex', gap:20}}>
        <div style={{flex:1}}>
          <div style={{marginBottom:8}}>
            <label>Page size: <input type="number" value={pageSize} onChange={e => setPageSize(Number(e.target.value)||20)} style={{width:80}} /></label>
          </div>
          <table border="1" cellPadding={6} style={{width:'100%'}}>
            <thead><tr><th>Id</th><th>Key</th><th>User</th><th>Txn</th><th>Created</th><th>Actions</th></tr></thead>
            <tbody>
              {paged.map(k => (
                <tr key={k.id}>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.id}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.keyValue}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.userId}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{k.transactionId}</td>
                  <td onClick={() => fetchKey(k)} style={{cursor:'pointer'}}>{new Date(k.createdAt).toLocaleString()}</td>
                  <td><button onClick={() => deleteKey(k)}>Delete</button></td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{marginTop:8}}>
            <button onClick={() => fetchList(Math.max(0, page-1), pageSize)} disabled={page===0}>Prev</button>
            <span style={{margin: '0 8px'}}>Page {page+1}</span>
            <button onClick={() => fetchList(page+1, pageSize)} disabled={keys.length < pageSize}>Next</button>
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
    </div>
  );
}
