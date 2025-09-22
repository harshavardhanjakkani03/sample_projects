import React, { useState } from 'react'
import { createRoot } from 'react-dom/client'
import Register from './pages/Register'
import AdminPending from './pages/AdminPending'
import IdempotencyAdmin from './pages/IdempotencyAdmin'
import Transactions from './pages/Transactions'

function App(){
  const [page, setPage] = useState('register')
  return (
    <div>
      <nav style={{marginBottom:12}}>
        <button onClick={()=>setPage('register')}>Register</button>
        <button onClick={()=>setPage('admin')}>Admin</button>
  <button onClick={()=>setPage('tx')}>Transactions</button>
  <button onClick={()=>setPage('idem')}>Idempotency</button>
      </nav>
      {page==='register' && <Register />}
      {page==='admin' && <AdminPending />}
  {page==='tx' && <Transactions />}
  {page==='idem' && <IdempotencyAdmin />}
    </div>
  )
}

createRoot(document.getElementById('root')).render(<App />)
