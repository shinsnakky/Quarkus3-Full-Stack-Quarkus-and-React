import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './auth/Login';
import { Tasks } from './tasks/Tasks';
import { Users } from './users/Users';

export const App = () => (
  <BrowserRouter>
    <Routes>
      <Route exact path='/' element={<Navigate to='/tasks/pending' />} />
      <Route exact path='/login' element={<Login />} />
      <Route exact path='/tasks' element={<Tasks />} />
      <Route exact path='/tasks/project/:projectId' element={<Tasks />} />
      <Route exact path='/tasks/pending'
        element={<Tasks title='Todo' filter={t => !Boolean(t.complete)} />} />
      <Route exact path='/tasks/completed' element={<Tasks title='Completed' 
        filter={t => Boolean(t.complete)} />}/>
      <Route exact path='/users' element={<Users />} />
    </Routes>
  </BrowserRouter>
);
