// Minimal e2e runner using node fetch to exercise registration -> approval -> deposit
// Run: node ./scripts/e2e-runner.js (from frontend)

const fetch = globalThis.fetch || require('node-fetch')

async function run(){
  const base = process.env.BASE || 'http://localhost:8080'
  console.log('Using base', base)

  // 1. register
  const reg = await fetch(base + '/api/register', { method: 'POST', headers: {'content-type':'application/json'}, body: JSON.stringify({ name: 'E2E User', email: 'e2e@example.com', password: 'secret' }) })
  console.log('register status', reg.status)
  const user = await reg.json()
  console.log('user', user)
  const id = user.id

  // 2. approve
  const app = await fetch(base + '/api/admin/users/' + id + '/approve', { method: 'POST' })
  console.log('approve status', app.status)

  // 3. deposit
  const idem = 'e2e-' + Date.now()
  const tx = await fetch(base + '/api/transactions?userId=' + id + '&amount=50&type=deposit&channel=UPI', { method: 'POST', headers: { 'Idempotency-Key': idem } })
  console.log('deposit status', tx.status)
  const tjson = await tx.json()
  console.log('tx', tjson)
}

run().catch(e=>{ console.error(e); process.exit(1) })
