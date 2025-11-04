import { Route, Routes } from 'react-router-dom'

import NotFoundView from './not-found'

import Home from './home'

const ROUTES = {
  not_found: '/*',
}

export function Router () {
  return (
    <Routes>
      <Route
        index
        element={<Home />}
      >
      </Route>
      <Route
        path={ROUTES.not_found}
        element={<NotFoundView />}
      >
      </Route>
    </Routes>
  )
}