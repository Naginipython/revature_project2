import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, TextField, Typography } from "@mui/material";
import { EnvelopeListCard } from "./EnvelopeListCard";
import { Envelope } from './AddMoneyInterfaces';
import useStore from "../../stores";
import { UserInfo } from "../../stores";
import "./AddMoney.css";

export const AddMoney: React.FC = () => {
    const [envs, setEnvs] = useState<Envelope[]>([]);

    const [title, setTitle] = useState("");
    const [desc, setDesc] = useState("");
    const [amount, setAmount] = useState("");

    const [enableBtn, setEnableBtn] = useState(true);
    const [error, setError] = useState("");

    const user: UserInfo = useStore((state: any) => state.user);

    const navigate = useNavigate();

    const handleForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (
            envs.reduce((acc, env) => acc + parseInt(env.amount), 0) == parseInt(amount) &&
            parseInt(amount) > 0
        ) {
            const fetchPromises = envs.map((env) => {
                if (parseInt(env.amount) > 0) {
                    return fetch(`http://localhost:8080/envelopes/allocate/${env.envelope_id}`, {
                        method: "POST",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer " + user.token
                        },
                        body: JSON.stringify({
                            title: title,
                            transactionDescription: desc,
                            transactionAmount: parseInt(env.amount),
                            category: ""
                        }),
                    });
                }
                return Promise.resolve();
            });

            await Promise.all(fetchPromises);
            navigate("/envelopes");
        }
    };
    const handleAmountChange = (index: number, newAmount: string) => {
        let temp = [...envs];
        temp[index].amount = newAmount;
        setEnvs(temp);
    };

    // Fetches all envelopes
    useEffect(() => {
        fetch("http://localhost:8080/envelopes", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + user.token
            },
            credentials: "include"
        }).then((response) => {
            return response.json();
        }).then((data) => {
            let temp = [];
            for (let i = 0; i < data.length; i++) {
                temp.push({
                    envelope_id: data[i].envelopeId,
                    user_id: user.userId,
                    envelope_description: data[i].envelopeDescription,
                    balance: data[i].balance,
                    max_limit: data[i].maxLimit,
                    amount: "0",
                    setAmount: null
                });
            }
            setEnvs(temp);
        });
    }, [user]);

    // Error checks amount usage as envelopes fill
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
                        onChange={(e) => setTitle(e.target.value)}
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
                        required
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
                                <EnvelopeListCard 
                                    colorClass={color} 
                                    envelope={env} 
                                    onAmountChange={(newAmount) => handleAmountChange(index, newAmount)}
                                />
                            </div>
                        );
                    })}
                </Box>
            </Box>
        </Box>
    );
}
