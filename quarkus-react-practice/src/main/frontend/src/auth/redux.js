import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const login = createAsyncThunk(
  'auth/login',
  async ({name, password}, thunkAPI) => {
    const response = await fetch(
      `${process.env.REACT_APP_API_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({name, password}),
      }
    );
    if (!response.ok) {
      return thunkAPI.rejectWithValue({
        status: response.status, statusText: response.statusText,
        data: response.data
      });
    }
    return response.text();
  }
);

export const authSlice = createSlice({
  name: 'auth',
  initialState: {
    jwt: sessionStorage.getItem('jwt')
  },
  reducers: {
    logout: state => {
      sessionStorage.removeItem("jwt");
      state.jwt = null;
    }
  },
  extraReducers: builder => {
    builder.addCase(login.fulfilled, (state, action) => {
      sessionStorage.setItem('jwt', action.payload);
      state.jwt = action.payload;
    })
  }
}); 
