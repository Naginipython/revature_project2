import { useEffect, useState } from "react";
import useStore from "../../stores";
import { UserInfo } from "../../stores";
import { useNavigate } from "react-router-dom";
import { Accordion, AccordionDetails, AccordionSummary, Box, Button, Card, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogTitle, Grid2, IconButton, ListItem, Menu, MenuItem, Snackbar, Stack, TextField, Typography } from "@mui/material";
import axios from "axios";
import { CloseOutlined, ExpandCircleDown } from "@mui/icons-material";
import backendHost from "../../backendHost";


interface Envelope {
  envelopeId: number;
  user: any;
  envelopeDescription: string;
  maxLimit: number;
  balance: number;
}

interface Transaction {
  transactionId: number;
  title: string;
  transactionAmount: number;
  datetime: Date;
  transactionDescription: string;
  category: string;
  envelope: Envelope;
}


export const AllTransactions: React.FC = () => {
  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");

  const [transactions, setTransactions] = useState<Transaction[]>([]);

  const user: UserInfo = useStore((state: any) => state.user);

  const navigate = useNavigate();
  const [edited, setEdited] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [filterMenu, setFilterMenu] = useState(false);
  const [transactiontoEdit, setTransactiontoEdit] = useState<Transaction>({
    transactionId: 0,
    title: "",
    transactionAmount: 0,
    datetime: new Date(),
    transactionDescription: "",
    category: "",
    envelope: {
      envelopeId: 0,
      user: null,
      envelopeDescription: "",
      maxLimit: 0,
      balance: 0,
    }
  });

   const [allCategories, setAllCategories] = useState<string[]>([]);
   const [filteredCategory, setFilteredCategory] = useState<string>("All");

  const toastAlert = (message: string) => {
    setAlertMessage(message);
    setShowAlert(true);
  };

  // Transaction editing functions
  const changeEditTransactionValues = (event: any) => {
    setTransactiontoEdit({
      ...transactiontoEdit,
      [event.target.name]: event.target.value,
    });
    setEdited(true);
  };

  const editTransactionValues = async (event: any) => {
    event.preventDefault();

    let currentTransaction = transactions.find(
      (transaction) =>
        transaction.transactionId === transactiontoEdit.transactionId
    );
    let different = false;

    if (currentTransaction?.title !== transactiontoEdit.title) {
      different = true;
      try {
        await axios.patch(
          `${backendHost}/transactions/title/${transactiontoEdit.transactionId}`,
          transactiontoEdit.title,
          {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "text/plain",
            },
            withCredentials: true,
          }
        );
      } catch (err) {
        toastAlert("Error editing transaction title.");
      }
    }

    if (
      currentTransaction?.transactionDescription !==
      transactiontoEdit.transactionDescription
    ) {
      different = true;
      try {
        await axios.patch(
          `${backendHost}/transactions/description/${transactiontoEdit.transactionId}`,
          transactiontoEdit.transactionDescription,
          {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "text/plain",
            },
            withCredentials: true,
          }
        );
      } catch (err) {
        toastAlert("Error editing transaction description.");
      }
    }

    if (currentTransaction?.category !== transactiontoEdit.category) {
      different = true;
      try {
        await axios.patch(
          `${backendHost}/transactions/category/${transactiontoEdit.transactionId}`,
          transactiontoEdit.category,
          {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "text/plain",
            },
            withCredentials: true,
          }
        );
      } catch (err) {
        toastAlert("Error editing transaction category.");
      }
    }

    if (different) {
      toastAlert("Transaction edited successfully!");
      let newCategories: string[] = [];
      setTransactions(
        transactions.map((transaction) => {
          if (transaction.transactionId === transactiontoEdit.transactionId) {
            newCategories.push(transactiontoEdit.category);
            return {
              ...transaction,
              title: transactiontoEdit.title,
              transactionDescription: transactiontoEdit.transactionDescription,
              category: transactiontoEdit.category,
            };
          } else {
            newCategories.push(transaction.category);
            return transaction;
          }
        })
      );
      setAllCategories([...new Set(newCategories)]);
    } else {
      toastAlert("No changes made to transaction.");
    }
  };

  // Fetch transactions on page load
  useEffect(() => {
    if (user.loggedIn){
        axios
        .get(`${backendHost}/transactions`, {
            headers: {
            Authorization: `Bearer ${user.token}`,
            "Content-Type": "application/json",
            },
            withCredentials: true,
        })
        .then((response) => {
            console.log(response.data);
            let fetchedTransactions = [];
            for (let i = 0; i < response.data.length; i++) {
                let transaction = response.data[i];
                if (user.role==="ROLE_MANAGER" || transaction.envelope.user.userId === user.userId) {
                    let newTransaction = {
                        transactionId: transaction.transactionId,
                        title: transaction.title,
                        transactionAmount: transaction.transactionAmount,
                        datetime: new Date(transaction.datetime),
                        transactionDescription: transaction.transactionDescription,
                        category: transaction.category,
                        envelope: transaction.envelope
                        };
                    fetchedTransactions.push(newTransaction);
                }
            }
            setTransactions(fetchedTransactions);
            setAllCategories([
                ...new Set(
                fetchedTransactions.map((transaction) => transaction.category)
                ),
            ]);
            
        })
        .catch((err) => {
            console.error(err);
        });
    }

  }, [user]);

  return (
    <Box sx={{ padding: "20px", height: "calc(100vh - 20px - 50px)" }}>
      <Grid2
        container
        spacing={2}
        sx={{
          margin: "auto",
          maxWidth: { xs: "90%", md: "75%" },
          height: "100%",
          overflow: "auto"
        }}
        id="transacationContainer"
      >
        <Grid2 size={12}>
          <Card variant="outlined" sx={{ boxShadow: 3 }}>
            <CardHeader
              title={
                <>
                  <Stack
                    direction="row"
                    sx={{ justifyContent: "space-between" }}
                  >
                    <Typography variant="h5" sx={{ fontWeight: "bold" }}>
                     {user.role==="ROLE_MANAGER"?"All Transactions":"Transactions for " + user.firstName + " " + user.lastName} 
                    </Typography>

                    {/* Filter and Options buttons */}
                    <Button
                      id="categoryButton"
                      size="small"
                      onClick={() => {
                        setFilterMenu(true);
                      }}
                    >
                      Filter{filteredCategory === "All" ? "" : `: ${filteredCategory}`}
                    </Button>
                  </Stack>
                </>
              }
            />

            {/* mapping out transactions to display in accordions. Sorting by transaction_id to display most recent transactions first. */}
            <CardContent sx={{ overflowY: "auto" }}>
              {transactions.length === 0 ? (
                <Typography variant="h4" sx={{ textAlign: "center" }}>
                  No transactions.{" "}
                </Typography>
              ) : (
                transactions
                  .filter(
                    (transaction) =>
                      transaction.category === filteredCategory ||
                      filteredCategory === "All"
                  )
                  .sort((a, b) => b.datetime.getTime() - a.datetime.getTime())
                  .map((transaction) => (
                    <Accordion sx={{ boxShadow: 1 }}>
                      <AccordionSummary
                        key={transaction.transactionId}
                        expandIcon={<ExpandCircleDown />}
                      >
                        <Stack
                          direction="row"
                          spacing={2}
                          sx={{
                            justifyContent: "space-between",
                            width: "95%",
                          }}
                        >
                          <ListItem>
                            <Typography sx={{ fontWeight: "bold" }}>
                              {transaction.title}
                            </Typography>
                          </ListItem>
                          <ListItem>
                            <Stack
                              direction={{
                                xs: "column-reverse",
                                md: "row",
                              }}
                              sx={{
                                justifyContent: "space-between",
                                width: "100%",
                              }}
                            >
                              <Typography color="palette.secondary.light">
                                {transaction.datetime.getMonth() + 1}/
                                {transaction.datetime.getDate()}/
                                {transaction.datetime
                                  .getFullYear()
                                  .toString()
                                  .substr(-2)}
                              </Typography>
                              {transaction.transactionAmount > 0 ? (
                                <Typography
                                  sx={{
                                    color: "green",
                                    fontWeight: "bold",
                                  }}
                                >
                                  ${transaction.transactionAmount}
                                </Typography>
                              ) : (
                                <Typography
                                  sx={{
                                    color: "red",
                                    fontWeight: "bold",
                                  }}
                                >
                                  -$
                                  {Math.abs(transaction.transactionAmount)} 
                                </Typography>
                              )}
                              <Typography>{transaction.envelope.envelopeDescription} 
                                {user.role==="ROLE_MANAGER" && user.userId != transaction.envelope.user.userId ?" (" + transaction.envelope.user.firstName + " " + transaction.envelope.user.lastName+")":""}
                              </Typography>
                            </Stack>
                          </ListItem>
                        </Stack>
                      </AccordionSummary>
                      <AccordionDetails>
                        <Stack
                          direction="row"
                          spacing={2}
                          sx={{
                            width: "100%",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          <Typography>
                            {transaction.transactionDescription}
                          </Typography>
                          <Typography sx={{ fontWeight: "bold" }}>
                            {transaction.category}
                          </Typography>
                          <Button
                            className="editButton"
                            onClick={() => {
                              setTransactiontoEdit(transaction);
                              setEdited(false);
                              setOpenEdit(true);
                            }}
                          >
                            Edit
                          </Button>
                        <Button
                            variant="contained"
                            onClick={()=>{
                                navigate(`/envelope/${transaction.envelope.envelopeId}`)
                            }}
                          >
                           Envelope
                          </Button>
                        </Stack>
                      </AccordionDetails>
                    </Accordion>
                  ))
              )}
            </CardContent>
          </Card>
        </Grid2>

        {/* Dialog for editing transactions */}
        <Dialog
          open={openEdit}
          onClose={() => {
            setOpenEdit(false);
          }}
          fullWidth
          maxWidth="sm"
        >
          <DialogTitle>Edit Transaction</DialogTitle>
          <DialogContent>
            <br />
            <Stack direction="column" spacing={2}>
              <TextField
                id="editTitle"
                name="title"
                label="Title"
                defaultValue={transactiontoEdit.title}
                onChange={changeEditTransactionValues}
                error={transactiontoEdit.title === ""}
              />
              <TextField
                id="editTransactionDescription"
                name="transactionDescription"
                label="Description"
                multiline
                maxRows={5}
                error={transactiontoEdit.transactionDescription === ""}
                defaultValue={transactiontoEdit.transactionDescription}
                onChange={changeEditTransactionValues}
              />
              <TextField
                id="editTransactionCategory"
                name="category"
                label="Category"
                error={transactiontoEdit.category === ""}
                defaultValue={transactiontoEdit.category}
                onChange={changeEditTransactionValues}
              />
            </Stack>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={() => {
                setOpenEdit(false);
              }}
            >
              Cancel
            </Button>
            <Button
              disabled={
                !edited ||
                transactiontoEdit.title === "" ||
                transactiontoEdit.transactionDescription === "" ||
                transactiontoEdit.category === ""
              }
              type="submit"
              onClick={(e) => {
                editTransactionValues(e);
                setOpenEdit(false);
              }}
            >
              Submit
            </Button>
          </DialogActions>
        </Dialog>

        <Snackbar
          open={showAlert}
          autoHideDuration={6000}
          onClose={() => {
            setShowAlert(false);
          }}
          message={alertMessage}
          action={
            <>
              <IconButton
                size="small"
                aria-label="close"
                color="inherit"
                onClick={() => {
                  setShowAlert(false);
                }}
              >
                <CloseOutlined fontSize="small" />
              </IconButton>
            </>
          }
        />

        {/* Menu for filtering transactions */}
        <Menu
          open={filterMenu}
          onClose={() => {
            setFilterMenu(false);
          }}
          anchorEl={document.getElementById("categoryButton")}
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "right",
          }}
          sx={{ overflowY: "auto" }}
        >
          <MenuItem
            sx={{ fontWeight: "bold" }}
            onClick={() => {
              setFilteredCategory("All");
              setFilterMenu(false);
            }}
          >
            All
          </MenuItem>
          <MenuItem sx={{ fontWeight: "bold" }} disabled>
            Category
          </MenuItem>
          {allCategories.map((category) => {
            return (
              <MenuItem
                onClick={() => {
                  setFilteredCategory(category);
                  setFilterMenu(false);
                }}
              >
                {category}
              </MenuItem>
            );
          })}
        </Menu>
      </Grid2>
    </Box>
  );
};
