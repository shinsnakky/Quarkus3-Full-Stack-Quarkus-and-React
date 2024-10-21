import { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Box, Toolbar } from '@mui/material';
import { layoutSlice } from './redux'
import { TopBar } from './TopBar';
import { MainDrawer } from './MainDrawer';
import { api } from '../projects/api';
import { NewProjectDialog } from '../projects/NewProjectDialog';
import { EditTask } from '../tasks/EditTask';
import { ChangePasswordDialog } from '../users/ChangePasswordDialog';

export const Layout = ({children}) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();  
  const jwt = useSelector(state => state.auth.jwt);
  
  useEffect(() => {
    if (!jwt) {
      navigate('/login');
    }
  }, [navigate, jwt]);
  
  const drawerOpen = useSelector(state => state.layout.drawerOpen);
  const doToggleDrawer = () => dispatch(layoutSlice.actions.toggleDrawer());
  const {data: projects} = api.endpoints.getProjects.useQuery(
    undefined, {pollingInterval: 10000}
  );
  const doOpenNewProject = () => dispatch(
    layoutSlice.actions.openNewProject()
  );

  return (
    <Box sx={{display: 'flex'}}>
      <TopBar goHome={() => navigate('/')} newTask={() => {/* TODO */}}
        toggleDrawer={doToggleDrawer} drawerOpen={drawerOpen} />
      <MainDrawer toggleDrawer={doToggleDrawer} drawerOpen={drawerOpen}
        openNewProject={doOpenNewProject} projects={projects} />
      <Box sx={{flex: 1}}>
        <Toolbar />
        <Box component='main'>{children}</Box>
      </Box>
      <EditTask />
      <NewProjectDialog />
      <ChangePasswordDialog />
    </Box>
  );
}; 
