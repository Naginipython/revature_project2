import React from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import { Link, useNavigate } from "react-router-dom";
import useStore from "../../stores";
import "./Navbar.css";

export const Navbar: React.FC = () => {
  const { user, setUser } = useStore(); // Access global user state and updater function
  const navigate = useNavigate();

  const handleLogout = () => {
    setUser({
      loggedIn: false,
      userId: -1,
      username: "",
      role: "",
      firstName: "",
      lastName: "",
      token: "",
    });
    navigate("/");
  };

  return (
    <AppBar position="static" id="appbar">
      <Toolbar>
        <Typography variant="h6" id="navbar-title" sx={{ flexGrow: 1 }}>
          Project Name
        </Typography>
        <div className="button-container">
          {user.loggedIn ? (
            <Button
              className="oval-button"
              color="inherit"
              onClick={handleLogout}
            >
              Logout
            </Button>
          ) : (
            <>
              <Button
                className="oval-button"
                color="inherit"
                component={Link}
                to="/"
              >
                Login
              </Button>
              <Button
                className="oval-button"
                color="inherit"
                component={Link}
                to="/register"
              >
                Register
              </Button>
            </>
          )}
        </div>
      </Toolbar>
    </AppBar>
  );
};
