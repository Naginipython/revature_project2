import { Box, Typography } from "@mui/material";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Paper } from "@mui/material";
import { useEffect, useState } from "react";
import useStore, { UserInfo } from "../../stores";

interface User {
    userId: number,
	firstName: string,
	lastName: string
	username: string,
    email: string,
	role: string,
}

export default function () {
    const u: UserInfo = useStore((state: any) => state.user);
    const [users, setUsers] = useState<User[]>([]);

    useEffect(() => {
        // Fetch users from the server
        console.log(u);
        fetch("/users", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + u.token,
            }
        }).then(res => {
            return res.json();
        }).then(body => {
            console.log(body);
            if (!body.hasOwnProperty("error")) {
                setUsers(body);
            }
        })
    }, [u]);

    const handleDeleteUser = (username: string) => {
        // Delete user
        fetch(`/users/${username}`, {
            method: "DELETE",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + u.token,
            }
        }).then(res => {
            return res.json();
        }).then(_body => {
            // TODO: check failed
            setUsers(users.filter(user => user.username !== username));
        });
    };

    // const handlePromoteUser = (username: string) => {

    // }

    return (
        <Box sx={{ width: "75vw", margin: "auto", paddingTop: "2rem" }}>
            <Typography variant="h4" gutterBottom>
                All Users
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ width: "50px" }}>ID</TableCell>
                            <TableCell>Name</TableCell>
                            <TableCell>Username</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell sx={{ width: "150px" }}>Role</TableCell>
                            <TableCell sx={{ width: "100px" }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.userId}>
                                <TableCell sx={{ width: "50px" }}>{user.userId}</TableCell>
                                <TableCell>{user.firstName} {user.lastName}</TableCell>
                                <TableCell>{user.username}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell sx={{ width: "150px" }}>{user.role}</TableCell>
                                <TableCell sx={{ width: "100px" }}>
                                    {/* {user.role == "ROLE_EMPLOYEE" && //250px with both buttons
                                        <Button variant="contained" color="secondary" sx={{ marginRight: "1rem" }}>
                                            Promote
                                        </Button>
                                    } */}
                                    {user.userId != u.userId &&
                                        <Button variant="contained" color="secondary" onClick={() => handleDeleteUser(user.username)}>
                                            Delete
                                        </Button>
                                    }  
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
}
