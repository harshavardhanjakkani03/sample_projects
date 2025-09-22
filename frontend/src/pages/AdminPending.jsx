import React, { useEffect, useState } from 'react'

export default function AdminPending(){
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchPending = async () => {
    setLoading(true)
    try {
      const r = await fetch('/api/admin/pending-users')
      if (!r.ok) throw new Error('Status ' + r.status)
      const data = await r.json()
      setUsers(data)
    } catch (e) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }

  useEffect(()=>{ fetchPending() }, [])

  const approve = async (id) => {
    try {
      const r = await fetch('/api/admin/users/' + id + '/approve', { method: 'POST' })
      if (!r.ok) throw new Error('Approve failed ' + r.status)
      await fetchPending()
    } catch (e) {
      setError(String(e))
    }
  }

  return (
    <main>
      <h2>Pending Users</h2>
      {loading && <p>Loading...</p>}
      {error && <p style={{color:'red'}}>{error}</p>}
      <ul>
        {users.map(u => (
          <li key={u.id} style={{marginBottom:8}}>
            {u.email} — {u.name} {' '}
            <button onClick={() => approve(u.id)}>Approve</button>
          </li>
        ))}
      </ul>
    </main>
  )
}
