import { combineReducers, configureStore } from '@reduxjs/toolkit';
import { layoutSlice } from './layout/redux';

const appReducer = combineReducers({
  layout: layoutSlice.reducer
});

const rootReducer = (state, action) => {
  return appReducer(state, action);
};

export const store = configureStore({
  reducer: rootReducer
});
