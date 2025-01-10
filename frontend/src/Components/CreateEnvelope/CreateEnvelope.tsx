import { Box, Button, Divider, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import useStore, { UserInfo } from "../../stores";
import "./CreateEnvelope.css";
import { useNavigate } from "react-router-dom";


export const CreateEnvelope: React.FC = () => {
    const [name, setName] = useState("");
    const [balance, setBalance] = useState("");
    const [limit, setLimit] = useState("");
    const [error, setError] = useState("");
    const [btnDisabled, setBtnDisabled] = useState(true);

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
        fetch("http://localhost:8080/envelopes", {
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
            navigate("/envelopes");
        })
    };

    //  Shamelessly copied from Login
    return (
        <Box className="env-container">
            {/* Left Side: Text */}
            <Box className="env-text">
                <Typography variant="h3" gutterBottom>
                    Start Your Budget Now!
                </Typography>
                <Typography variant="body1">
                    Filling out a budget form is{" "}
                    <span className="highlight-text">super simple </span>
                    and helps you stay on top of your finances! Start by{" "}
                    <span className="highlight-text">giving your budget a name</span>, like{" "}
                    <span className="highlight-text">"Grocery Fund"</span> or{" "}
                    <span className="highlight-text">"Vacation Dreams"</span>,{" "}
                    to make it personal and fun. Then, add your
                    <span className="highlight-text">current balance so you know exactly where you stand</span>.{" "}
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