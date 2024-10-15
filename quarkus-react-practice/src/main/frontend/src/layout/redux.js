import { createSlice } from '@reduxjs/toolkit';

export const layoutSlice = createSlice({
  name: 'layout',
  initialState: {
    drawerOpen: true,
  },
  reducers: {
    toggleDrawer: state => {
      state.drawerOpen = !state.drawerOpen;
    }
  }
});
