import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { layoutSlice } from './layout/redux';
import { authSlice } from './auth/redux';
import { api as userApi } from './users/api';
import { api as projectApi } from './projects/api';
import { api as taskApi } from './tasks/api';

const appReducer = combineReducers({
  auth: authSlice.reducer,
  layout: layoutSlice.reducer,
  users: userApi.reducer,
  projects: projectApi.reducer,
　tasks: taskApi.reducer
  //[userApi.reducerPath]: userApi.reducer
});

const { logout } = authSlice.actions;

const rootReducer = (state, action) => {
  if (logout.match(action)) {
    state = undefined;
  }
  return appReducer(state, action);
};

export const store = configureStore({
  reducer: rootReducer,
  middleware: getDefaultMiddleware => getDefaultMiddleware()
    .concat(projectApi.middleware)
    .concat(taskApi.middleware)
    .concat(userApi.middleware)
}); 
