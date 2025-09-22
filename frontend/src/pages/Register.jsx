import React, { useState } from 'react'

export default function Register(){
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState(null)

  const onSubmit = async (e) => {
    e.preventDefault()
    setMessage('Submitting...')
    try {
      const resp = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
      })
      if (!resp.ok) {
        const txt = await resp.text()
        setMessage('Error: ' + resp.status + ' ' + txt)
        return
      }
      const data = await resp.json()
      setMessage('Registered user id: ' + (data && data.id ? data.id : JSON.stringify(data)))
    } catch (err) {
      setMessage('Fetch error: ' + String(err))
    }
  }

  return (
    <main>
      <h1>Register</h1>
      <form onSubmit={onSubmit}>
        <label style={{display:'block'}}>Name<input value={name} onChange={e=>setName(e.target.value)} /></label>
        <label style={{display:'block'}}>Email<input value={email} onChange={e=>setEmail(e.target.value)} /></label>
        <label style={{display:'block'}}>Password<input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></label>
        <button type="submit">Register</button>
      </form>
      {message && <p>{message}</p>}
    </main>
  )
}
