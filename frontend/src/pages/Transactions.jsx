import React, { useEffect, useState } from 'react'

export default function Transactions(){
  const [userId, setUserId] = useState('')
  const [amount, setAmount] = useState('')
  const [history, setHistory] = useState([])
  const [msg, setMsg] = useState(null)

  const fetchHistory = async () => {
    if (!userId) return
    const r = await fetch(`/api/users/${userId}/transactions`)
    if (r.ok) setHistory(await r.json())
  }

  useEffect(()=>{ fetchHistory() }, [userId])

  const doTxn = async (type) => {
    setMsg('Processing...')
    try {
      const idem = 'ui-' + Date.now()
      const r = await fetch(`/api/transactions?userId=${userId}&amount=${amount}&type=${type}&channel=ATM`, {
        method: 'POST',
        headers: { 'Idempotency-Key': idem }
      })
      if (!r.ok) throw new Error('Status ' + r.status)
      const t = await r.json()
      setMsg('Txn id: ' + t.id)
      await fetchHistory()
    } catch (e) {
      setMsg('Error: ' + e)
    }
  }

  return (
    <main>
      <h2>Transactions</h2>
      <label style={{display:'block'}}>UserId <input value={userId} onChange={e=>setUserId(e.target.value)} /></label>
      <label style={{display:'block'}}>Amount <input value={amount} onChange={e=>setAmount(e.target.value)} /></label>
      <button onClick={()=>doTxn('deposit')}>Deposit</button>
      <button onClick={()=>doTxn('withdraw')}>Withdraw</button>
      {msg && <p>{msg}</p>}
      <h3>History</h3>
      <ul>
        {history.map(h => <li key={h.id}>{h.id} {h.type} {h.amount}</li>)}
      </ul>
    </main>
  )
}
