import { Box, Button, Divider, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import useStore, { UserInfo } from "../../stores";
import "./CreateEnvelope.css";
import { useNavigate } from "react-router-dom";
import backendHost from "../../backendHost";


export const CreateEnvelope: React.FC = () => {
    const [name, setName] = useState("");
    const [balance, setBalance] = useState("");
    const [limit, setLimit] = useState("");
    const [error, setError] = useState("");
    const [btnDisabled, setBtnDisabled] = useState(true);
    const setSnackbar = useStore((state) => state.setSnackbar);
    const user: UserInfo = useStore((state: any) => state.user);
    const navigate = useNavigate();

    useEffect(() => {
        if (name == "" || balance == "" || limit == "") {
            return;
        }
        const parsedBalance = parseFloat(balance);
        const parsedLimit = parseFloat(limit);
        if (isNaN(parsedBalance) || isNaN(parsedLimit) || parsedBalance < 0 || parsedLimit < 0) {
            setError("Balance and limit must be valid positive numbers.");
            setBtnDisabled(true);
            return;
        }
        if (parsedBalance > parsedLimit) {
            setError("Initial balance must be less than or equal to the limit.");
            setBtnDisabled(true);
            return
        }
        if (name == "") {
            setError("Name must not be empty.");
            setBtnDisabled(true);
            return;
        }
        setError("");
        setBtnDisabled(false);
    }, [name, balance, limit]);

    const handleCreateEnvelope = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const parsedBalance = parseFloat(balance);
        const parsedLimit = parseFloat(limit);
        fetch(`${backendHost}/envelopes`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + user.token,
            },
            body: JSON.stringify({
                userId: user.userId,
                envelopeDescription: name,
                balance: parseFloat(parsedBalance.toFixed(2)),
                maxLimit: parseFloat(parsedLimit.toFixed(2)),
            })
        }).then(res => {
            return res.json();
        }).then(body => {
            // TODO: handle failure
            console.log(body);
            setSnackbar(true, "Envelope created successfully!");
            navigate("/envelopes");
        })
    };

    //  Shamelessly copied from Login
    return (
        <Box className="env-container">
            {/* Left Side: Text */}
            <Box className="env-text">
                <Typography variant="h3" gutterBottom>
                    Let's get started!
                </Typography>
                <Typography variant="body1">
                    Creating an envelope is{" "}
                    <span className="highlight-text">super simple </span>
                    and helps you stay on top of your finances! Start by{" "}
                    <span className="highlight-text">giving your envelope a name</span>, like{" "}
                    <span className="highlight-text">"Grocery Fund"</span> or{" "}
                    <span className="highlight-text">"Vacation Dreams"</span>,{" "}
                    to make it personal and fun. Then, add your
                    <span className="highlight-text"> current balance so you know exactly where you stand</span>.{" "}
                    Finally, set a{" "}
                    <span className="highlight-text">spending limit to keep things in check</span>{" "}
                    and make sure your money works for you.

                </Typography>
            </Box>

            {/* Right Side: Form */}
            <Box className="env-form">
                <Typography variant="h4" align="center" gutterBottom>
                    Create Envelope
                </Typography>
                <Divider />
                <form onSubmit={handleCreateEnvelope}>
                    <TextField
                        className="textfield"
                        fullWidth
                        label="Envelope Name"
                        variant="outlined"
                        margin="normal"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <TextField
                        className="textfield"
                        fullWidth
                        label="Initial Balance"
                        variant="outlined"
                        margin="normal"
                        required
                        value={balance}
                        onChange={(e) => setBalance(e.target.value)}
                    />
                    <TextField
                        className="textfield"
                        fullWidth
                        label="Set Limit"
                        variant="outlined"
                        margin="normal"
                        required
                        value={limit}
                        onChange={(e) => setLimit(e.target.value)}
                    />
                    <Button
                        className="envButton1"
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ marginTop: 2, backgroundColor: btnDisabled ? "var(--purple)" : "" }}
                        disabled={btnDisabled}
                    >
                        Create Envelope
                    </Button>
                    <Typography variant="body1" sx={{ color: "red" }}>
                        {error}
                    </Typography>
                </form>
            </Box>
        </Box>
    )
}