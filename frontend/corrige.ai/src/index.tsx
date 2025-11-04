import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Application from './app'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Application />
  </StrictMode>,
)
