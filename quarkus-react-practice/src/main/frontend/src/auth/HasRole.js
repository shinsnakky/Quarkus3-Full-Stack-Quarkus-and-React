import { api } from '../users/api';

export const HasRole = ({role, children}) => {
  const {data} = api.endpoints.getSelf.useQuery();
  if ((data?.roles ?? []).includes(role)) {
    return children;
  }
  return null;
} 
