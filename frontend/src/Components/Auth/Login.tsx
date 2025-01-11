import React, { useState } from "react";
import { Box, Typography, TextField, Button, Divider } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.scss";

import useStore from "../../stores";
import backendHost from "../../backendHost";

export const Login: React.FC = () => {
  const [username, setUsername] = useState(""); // Changed to username
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

	const setUser = useStore((state) => state.setUser);
  const setSnackbar = useStore((state) => state.setSnackbar);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await axios.post(
        `${backendHost}/users`, 
        { username, password }, 
        { withCredentials: true } // Important for cookies/JWT
      ).then((res) => {
        console.log("Login successful:", res.data);
        // Set user information in the store
        // Change this to actual information later
        setUser({
          loggedIn: true,
    
          userId: res.data.userId,
          username: res.data.username,
          role: res.data.role,
          firstName: res.data.firstName,
          lastName: res.data.lastName,
          token: res.data.token
        });

        // Show the Snackbar
        setSnackbar(true, "Login successful!");
        
        // Set token in localstorage for later logins
				localStorage.setItem("gooderBudgetToken", res.data.token);

        // alert("Login successful!");
        navigate("/envelopes"); // Navigate to envelopes
      });

    } catch (err) {
      console.error("Login failed:", err);
    }
  };

  return (
    <Box className="login-container">
      {/* Left Side: Text */}
      <Box className="login-text">
        <Typography variant="h3" gutterBottom>
          Start Your Budget Now!
        </Typography>
        <Typography variant="body1">
          Here we aim to make{" "}
          <span className="highlight-text">managing your money simple</span> and
          <span className="highlight-text"> stress-free</span> by tracking your
          spending, setting goals, and keeping you on top of your finances—all
          in one easy place. With features like{" "}
          <span className="highlight-text">
            custom budgets, bill reminders,{" "}
          </span>
          and <span className="highlight-text">real-time updates, </span> they
          help you make smarter choices and{" "}
          <span className="highlight-text">
            take control of your finances effortlessly!
          </span>
        </Typography>
      </Box>

      {/* Right Side: Form */}
      <Box className="login-form">
        <Typography variant="h4" align="center" gutterBottom>
          Login
        </Typography>
        <Divider />
        <form onSubmit={handleLogin}>
          <TextField
            className="textfield"
            fullWidth
            label="Username" // Updated label to Username
            variant="outlined"
            margin="normal"
            value={username} // Changed to username
            onChange={(e) => setUsername(e.target.value)} // Handle username input
          />
          <TextField
            className="textfield"
            fullWidth
            label="Password"
            variant="outlined"
            margin="normal"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button
            className="button1"
            type="submit"
            fullWidth
            variant="contained"
            sx={{ marginTop: 2 }}
          >
            Log In
          </Button>
        </form>
        {/* Link to Register */}
        <Box mt={2}>
          <Typography variant="h6" align="center">
            New Here?{" "}
            <Link to="/register" className="signup-link">
              Sign up Now!
            </Link>
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};
