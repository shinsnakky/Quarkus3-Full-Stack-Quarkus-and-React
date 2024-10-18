import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { authSlice } from '../auth/redux';

/* CreateApi and fetchBaseQuery are utilities from RTK Query.
Reference documents are found at:
https://redux-toolkit.js.org/rtk-query/api/createApi
https://redux-toolkit.js.org/rtk-query/api/fetchBaseQuery
*/ 

const baseQuery = fetchBaseQuery({
  baseUrl: `${process.env.REACT_APP_API_URL}/users`,
  prepareHeaders: (headers, {getState}) => {
    headers.set('authorization', `Bearer ${getState().auth.jwt}`);
    return headers;
  }
});

const authBaseQuery = async (args, api, extraOptions) => {
    const result = await baseQuery(args, api, extraOptions);
    
    if (result.error && result.error.status === 401) {
      api.dispatch(authSlice.actions.logout());
    }
    return result;
}

/*
* I know this way of writing (which is the original) is better as this function
* can be reused for arbitrary paths.
* Just tried to separate the code into two parts to simplify the structure.   
const authBaseQuery = ({path}) => {
  const baseQuery = fetchBaseQuery({
    baseUrl: `${process.env.REACT_APP_API_URL}/${path}`,
    prepareHeaders: (headers, {getState}) => {
      headers.set('authorization', `Bearer ${getState().auth.jwt}`);
      return headers;
    }
  });
  return async (args, api, extraOptions) => {
    const result = await baseQuery(args, api, extraOptions);
    
    if (result.error && result.error.status === 401) {
      api.dispatch(authSlice.actions.logout());
    }
    return result;
  };
};
*/

export const api = createApi({
  reducerPath: 'users',
  // baseQuery: authBaseQuery({path: 'users'}),
  baseQuery: authBaseQuery,
  tagTypes: ['User'],
  endpoints: builder => ({
    getUser: builder.query({
      query: id => `/${id}`,
      providesTags: ['User']
    }),
    getUsers: builder.query({
      query: () => '/',
      providesTags: ['User']
    }),
    deleteUser: builder.mutation({
      query: user => ({
        url: `/${user.id}`,
        method: 'DELETE'
      }),
      invalidatesTags: ['User']
    }),
    getSelf: builder.query({
      query: () => '/self',
      providesTags: ['User']
    }),
    changePassword: builder.mutation({
      query: passwordChange => ({
        url: `/self/password`,
        method: 'PUT',
        body: passwordChange
      })
    })
  })
}); 


