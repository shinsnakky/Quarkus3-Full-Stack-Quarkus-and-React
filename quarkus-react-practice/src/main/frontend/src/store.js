import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { layoutSlice } from './layout/redux';
import { authSlice } from './auth/redux';
import { api as userApi } from './users/api';

const appReducer = combineReducers({
  auth: authSlice.reducer,
  layout: layoutSlice.reducer,
  users: userApi.reducer
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
    .concat(userApi.middleware)
}); 
