import { Box, Button, TextField, Typography } from "@mui/material";
import "./AddMoney.css";
import { useEffect, useState } from "react";
import { EnvelopeListCard } from "./EnvelopeListCard";
import { Envelope } from './AddMoneyInterfaces';

export const AddMoney: React.FC = () => {
    const [from, setFrom] = useState("");
    const [amount, setAmount] = useState("");
    const [desc, setDesc] = useState("");
    const [enableBtn, setEnableBtn] = useState(true);
    const [error, setError] = useState("");
    
    let envs: Envelope[] = [];

    const handleForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        console.log(from);
        console.log(desc);
        console.log(amount);
        if (
            envs.reduce((acc, env) => acc + parseInt(env.amount), 0) == parseInt(amount) && 
            parseInt(amount) > 0
        ) {
            // TODO: send data to backend
            // const response = await axios.post("http://localhost:8080/users", {
            //     username: "benm",
            //     password: "password"
            // }, {
            //     headers: {
            //         "Content-Type": "application/json"
            //     }
            // });
            // console.log(response.data);
        }
    };

    // TODO: fetch envelopes
    for (let i = 0; i < 5; i++) {
        const [amount2, setAmount2] = useState("0");
        envs.push({
            envelope_id: i,
            user_id: 1,
            envelope_description: "temp",
            balance: 50,
            max_limit: 50,
            envelope_history: [],
            amount: amount2,
            setAmount: setAmount2
        });
    }

    useEffect(() => {
        const sum = envs.reduce((acc, env) => acc + parseInt(env.amount), 0);
        if (amount == "" && sum != 0) {
            setError("Error: Amount not set yet");
            return;
        } else if (sum > parseInt(amount)) {
            setError("Error: set more than amount");
        } else {
            setError("");
            if (parseInt(amount) == sum) {
                setEnableBtn(false);
            } else {
                setEnableBtn(true);
            }
        }
    }, [envs, amount]);

    return (
        <Box className="main-add-money-container">
            <Box className="add-money-container">
                <Typography variant="h3" gutterBottom>
                    Add Money
                </Typography>
                <form onSubmit={handleForm} className="add-money-form">
                    <TextField
                        label="Recieved From?"
                        fullWidth
                        margin="normal"
                        onChange={(e) => setFrom(e.target.value)}
                        required
                    />
                    <TextField
                        label="Amount"
                        fullWidth
                        margin="normal"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        required
                    />
                    <TextField
                        label="Description"
                        fullWidth
                        margin="normal"
                        onChange={(e) => setDesc(e.target.value)}
                        multiline
                        rows={4}
                    />
                    <Button type="submit" variant="contained" color="primary" disabled={enableBtn}>
                        Add Money
                    </Button>
                </form>
            </Box>
            <Box className="add-envelope-container">
                <Box>
                    <Typography variant="h5" sx={{ color: "red", margin: "auto", width: "fit-content" }}>
                        {error}
                    </Typography>
                </Box>
                <Box className="envelope-list">
                    {envs.map((env, index) => {
                        let color = "envelope-header-warning";
                        if (env.balance == env.max_limit) {
                            color = "envelope-header-good";
                        } else if (env.balance < 0) {
                            color = "envelope-header-danger";
                        }

                        return (
                            <div style={{ height: "fit-content" }} key={index}>
                                <EnvelopeListCard colorClass={color} envelope={env} />
                            </div>
                        );
                    })}
                </Box>
            </Box>
        </Box>
    );
}