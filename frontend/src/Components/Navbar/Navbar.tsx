import React from "react";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Link from "@mui/material/Link";
import { useNavigate } from "react-router-dom";
import useStore from "../../stores";

export const Navbar: React.FC = () => {
  const { user, setUser } = useStore(); // Access global user state and updater function
  const setSnackbar = useStore((state) => state.setSnackbar);
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
    setSnackbar(true, "Logout successful!");
    navigate("/");
  };

  return (
    <AppBar position="static" id="appbar">
      <Toolbar>
        <Typography variant="h6" id="navbar-title" sx={{ flexGrow: 1 }}>
          Budget App
        </Typography>
        <div className="button-container">
          {user.loggedIn ? (
            <>
              <Link 
                variant="h5" 
                sx={{ color: "white", textDecorationColor: "white", cursor: "pointer", fontWeight: 600 }} 
                underline="always"
                onClick={() => navigate("/envelopes")}
              >
                Envelopes
              </Link>
              <Link 
                variant="h5" 
                sx={{ color: "white", textDecorationColor: "white", cursor: "pointer", fontWeight: 600 }} 
                underline="always"

                onClick={() => navigate("/transactions")}
              >
                Activity
              </Link>
              {user.role == "ROLE_MANAGER" &&
                <Link 
                  variant="h5" 
                  sx={{ color: "white", textDecorationColor: "white", cursor: "pointer", fontWeight: 600 }} 
                  underline="always"
                  onClick={() => navigate("/users")}
                >
                  Users
                </Link>
              }
              <Button
                className="oval-button"
                color="inherit"
                onClick={handleLogout}
              >
                Logout
              </Button>
            </>
          ) : (
            <>
              <Button
                className="oval-button"
                color="inherit"
                component={Link}
                onClick={() => navigate("/")}
              >
                Login
              </Button>
              <Button
                className="oval-button"
                color="inherit"
                component={Link}
                onClick={() => navigate("/register")}
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
