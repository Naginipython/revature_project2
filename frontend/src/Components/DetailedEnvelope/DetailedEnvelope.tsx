import {CloseOutlined, ExpandCircleDown } from "@mui/icons-material";
import { Accordion, AccordionDetails, AccordionSummary, Box, Button, Card, CardContent, CardHeader, CircularProgress, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Divider, Grid2, IconButton, InputAdornment, ListItem, Menu, MenuItem, Snackbar, Stack, TextField, Typography } from "@mui/material"
import { LineChart } from "@mui/x-charts";
import { useEffect, useState } from "react";
import {UserInfo} from "../../stores";
import useStore  from "../../stores";

import './DetailedEnvelope.scss';
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";
import { Envelope, EnvelopeHistory, OutgoingTransaction, Transaction } from "./DetailedEnvelopeInterfaces";

export const DetailedEnvelope:React.FC = () =>{

    const statusColors = { low: "#8b4dfe", high: "#23A455" };
    const [statusColor, setStatusColor] = useState(statusColors.high);

    const [envelope, setEnvelope] = useState<Envelope>({
      envelopeId: 0,
      user: null,
      envelopeDescription: "",
      maxLimit: 0,
      balance: 0,
    });
    
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [envelopeHistory, setEnvelopeHistory] = useState<EnvelopeHistory[]>([]);

    const [remaining, setRemaining] = useState(0);

    const [transactionMenu, setTransactionMenu] = useState(false);
    const [filterMenu, setFilterMenu] = useState(false);  

    const [openEdit, setOpenEdit] = useState(false);
    const [openCreate, setOpenCreate] = useState(false);
    const [transactiontoEdit, setTransactiontoEdit] = useState<Transaction>({transactionId: 0, title: "", transactionAmount: 0, datetime: new Date(), transactionDescription: "", category: "Spending"});
    const [transactiontoCreate, setTransactiontoCreate] = useState<OutgoingTransaction>({title: "", transactionAmount: 0, transactionDescription: "", category: ""});
    const [createAmountError, setCreateAmountError] = useState(false);
    const [edited, setEdited] = useState(false);

    const [allCategories, setAllCategories] = useState<string[]>([]);
    const[filteredCategory, setFilteredCategory] = useState<string>("All");
    
    const [deleteDialog, setDeleteDialog] = useState(false);

    const [loading, setLoading] = useState(true);
    const [showAlert, setShowAlert] = useState(false);
    const [alertMessage, setAlertMessage] = useState(""); 

    const user:UserInfo = useStore((state: any) => state.user);

    
    // get the value from URL to use as envelope id when routed from main envelope screen
    const { id } = useParams();
    const navigate = useNavigate();


    const toastAlert = (message:string) => {
      setAlertMessage(message);
      setShowAlert(true);
    }


    const deleteEnvelope = async() => {
      if (user.loggedIn) {
        axios.delete(`http://localhost:8080/envelopes/${id}`,{headers: {Authorization:`Bearer ${user.token}`, "Content-Type": "application/json"}, withCredentials: true})
        .then((response) => {
          console.log(response);
          toastAlert("Envelope deleted successfully!");
          navigate("/envelopes");
        })
        .catch((err) => {
          toastAlert("Error deleting envelope.");
          console.error(err);
        });
      }
    }

    // Transaction editing functions
    const changeEditTransactionValues = (event:any) =>{
      setTransactiontoEdit({...transactiontoEdit, [event.target.name]: event.target.value});
      setEdited(true);
    }

    const editTransactionValues = async(event:any) =>{
      event.preventDefault();

      let currentTransaction = transactions.find((transaction) => transaction.transactionId === transactiontoEdit.transactionId);
      let different = false;

      if (currentTransaction?.title !== transactiontoEdit.title) {
        different = true;
        try{
          await axios.patch(`http://localhost:8080/transactions/title/${transactiontoEdit.transactionId}`,transactiontoEdit.title,{headers: {Authorization:`Bearer ${user.token}`, "Content-Type": "text/plain"}, withCredentials: true});
          }
          catch(err){
            toastAlert("Error editing transaction title.");
        }

      }

      if (currentTransaction?.transactionDescription !== transactiontoEdit.transactionDescription) {
        different = true;
         try{
          await axios.patch(`http://localhost:8080/transactions/description/${transactiontoEdit.transactionId}`,transactiontoEdit.transactionDescription,{headers: {Authorization:`Bearer ${user.token}`, "Content-Type": "text/plain"}, withCredentials: true});
          }
          catch(err){
           toastAlert("Error editing transaction description.");
        }
      }

      if (currentTransaction?.category !== transactiontoEdit.category) {
        different = true;
         try{
          await axios.patch(`http://localhost:8080/transactions/category/${transactiontoEdit.transactionId}`,transactiontoEdit.category,{headers: {Authorization:`Bearer ${user.token}`, "Content-Type": "text/plain"}, withCredentials: true});
          }
          catch(err){
           toastAlert("Error editing transaction category.");
          }
      }

      if (different) {
        toastAlert("Transaction edited successfully!");
        let newCategories:string[] = [];
        setTransactions(
        transactions.map((transaction) => {
          if (transaction.transactionId === transactiontoEdit.transactionId) {
            newCategories.push(transactiontoEdit.category);
            return { ...transaction, title: transactiontoEdit.title, transactionDescription: transactiontoEdit.transactionDescription, category: transactiontoEdit.category };

          } else {
            newCategories.push(transaction.category);
            return transaction;
          }
        })
        );
        setAllCategories([...new Set(newCategories)]);
      }
      else{
        toastAlert("No changes made to transaction.");
      } 
      
    }

    // Transaction creation (Spending) functions
    const changeCreateTransactionValues = (event:any) =>{

      if (event.target.name === "transactionAmount") {
        if (parseInt(event.target.value) > envelope.balance) {
          setCreateAmountError(true);
          return;
        }
        else{
          setCreateAmountError(false);
        }
      }
        setTransactiontoCreate({...transactiontoCreate, [event.target.name]: event.target.value});
    }


    const handleCreateClose = () =>{
      setTransactiontoCreate({title: "", transactionAmount: 0, transactionDescription: "", category: "Spending"});
      setOpenCreate(false);
      setCreateAmountError(false);

    }

    const handleCreateOpen= () =>{
      setTransactiontoCreate({title: "", transactionAmount: 0, transactionDescription: "", category: "Spending"});
      setOpenCreate(true);
      setCreateAmountError(false);
    }

    const createTransaction = async(event:any) => {
      event.preventDefault();
      let newTransaction = {
        title: transactiontoCreate.title,
        transactionDescription: transactiontoCreate.transactionDescription,
        transactionAmount: transactiontoCreate.transactionAmount,
        category: transactiontoCreate.category,
      };
        axios.post(`http://localhost:8080/envelopes/spend/${id}`,newTransaction,{headers: {Authorization:`Bearer ${user.token}`, "Content-Type": "application/json"}, withCredentials: true})
        .then((response) => {
          setTransactions([...transactions, {transactionId: response.data.transactionId, title: response.data.title, transactionAmount: response.data.transactionAmount, datetime: new Date(response.data.datetime), transactionDescription: response.data.transactionDescription, category: response.data.category}]);
          setEnvelope({...envelope, balance: envelope.balance + response.data.transactionAmount});
          setEnvelopeHistory([...envelopeHistory, {amountHistoryId: 0, envelope: envelope, transaction: response.data, envelopeAmount: envelope.balance+ response.data.transactionAmount}]);
          setAllCategories([...new Set([...allCategories, response.data.category])]);
          setRemaining(remaining+response.data.transactionAmount);
          toastAlert("Transaction created successfully!");
          console.log(envelopeHistory);
        }).catch((err) => {
          toastAlert("Error creating transaction.");
          console.error(err);
        });
    }


    // Fetch envelope info, transactions, categories and envelope history at component mount
    useEffect(()=>{
      if(user.loggedIn){
        //fetch envelope info
        axios
          .get(`http://localhost:8080/envelopes/${id}`, {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "application/json",
            },
            withCredentials: true,
          })
          .then((response) => {
            setEnvelope({
              envelopeId: response.data.envelope_id,
              user: response.data.user,
              envelopeDescription: response.data.envelopeDescription,
              maxLimit: response.data.maxLimit,
              balance: response.data.balance,
            });
            setRemaining(response.data.maxLimit - response.data.balance);
            if (response.data.maxLimit - response.data.balance < 100) {
              setStatusColor(statusColors.low);
            }
          })
          .catch((err) => {
            toastAlert("Error fetching envelope info.");
            navigate("/envelopes");
            console.error(err);
          });

        //fetch transactions
        axios
          .get(`http://localhost:8080/transactions/envelope/${id}`, {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "application/json",
            },
            withCredentials: true,
          })
          .then((response) => {
            if (
              response.data.body ===
              `Transaction with Envelope id ${id} does not exist`
            ) {
              setTransactions([]);
            } else {
              let fetchedTransactions = [];
              for (let i = 0; i < response.data.body.length; i++) {
                let transaction = response.data.body[i];
                let newTransaction = {
                  transactionId: transaction.transactionId,
                  title: transaction.title,
                  transactionAmount: transaction.transactionAmount,
                  datetime: new Date(transaction.datetime),
                  transactionDescription: transaction.transactionDescription,
                  category: transaction.category,
                };
                fetchedTransactions.push(newTransaction);
              }
              setTransactions(fetchedTransactions);
              setAllCategories([...new Set(fetchedTransactions.map((transaction) => transaction.category))]);
            }
          })
          .catch((err) => {
            console.error(err);
          });

        // get envelope balance history
        axios
          .get(`http://localhost:8080/envelopes/history/${id}`, {
            headers: {
              Authorization: `Bearer ${user.token}`,
              "Content-Type": "application/json",
            },
            withCredentials: true,
          })
          .then((response) => {
            if (response.data.body === `Envelope history with Envelope id ${id} does not exist`) {
              setEnvelopeHistory([]);
            }
            else{
              let fetchedEnvelopeHistory = [];
              for (let i = 0; i < response.data.length; i++) {
                let envelopeHistory = response.data[i];
                let newEnvelopeHistory = {
                  amountHistoryId: envelopeHistory.amountHistoryId,
                  envelope: envelopeHistory.envelope,
                  transaction: envelopeHistory.transaction,
                  envelopeAmount: envelopeHistory.envelopeAmount,
                };
                fetchedEnvelopeHistory.push(newEnvelopeHistory);
              }
              setEnvelopeHistory(fetchedEnvelopeHistory);
            }
          })
          .catch((err) => {
            console.error(err);
          });
          
        setLoading(false);
    }
    },[user])

    return (
      <>
        {loading ? (
          <Box sx={{ display: "flex" }}>
            <CircularProgress size={40} />
          </Box>
        ) : (
          <>
            <br />
            <br />
            {/* using Grids from MUI to layout components. Root container element. */}
            <Grid2
              container
              spacing={2}
              sx={{
                margin: "auto",
                alignItems: "center",
                maxWidth: { xs: "90%", md: "70%" },
              }}
              id="mainContainer"
            >
              {/* Each subsequent child Grid2 element represents a row/column. Size of a row in a grid is 12, so we use size prop to adjust the width of the column. */}
              <Grid2
                size={12}
                sx={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                {/* card to display envelope details */}
                <Card
                  sx={{
                    justifyContent: "center",
                    alignItems: "center",
                    boxShadow: 5,
                    width: "100%",
                  }}
                  variant="outlined"
                >
                  {/* CardHeader component to display envelope description and remaining amount (balance - max limit) */}
                  <CardHeader
                    sx={{
                      backgroundColor: statusColor,
                      color: "white",
                      textAlign: "center",
                    }}
                    title={
                      <>
                        <Stack direction="row">
                          <ListItem
                            sx={{
                              justifyContent: "center",
                              alignItems: "center",
                            }}
                          >
                            <Typography
                              variant="h4"
                              sx={{ fontWeight: "bold", textAlign: "center" }}
                            >
                              {envelope.envelopeDescription}
                            </Typography>
                          </ListItem>
                          <ListItem
                            sx={{
                              justifyContent: "center",
                              alignItems: "center",
                            }}
                          >
                            <Typography
                              variant="h4"
                              sx={{ fontWeight: "bold", textAlign: "center" }}
                            >
                              ${envelope.balance}
                            </Typography>
                          </ListItem>
                        </Stack>
                      </>
                    }
                  />
                  {/* CardContent to display balance and max limit */}
                  <CardContent>
                    <Grid2
                      container
                      spacing={2}
                      sx={{
                        alignItems: "center",
                        justifyContent: "center",
                        textAlign: "center",
                      }}
                    >
                      <Grid2 size={6}>
                        <Typography variant="h5" sx={{}}>
                          Envelope Limit
                        </Typography>
                      </Grid2>
                      <Grid2 size={6}>
                        <Typography variant="h5">
                          ${envelope.maxLimit}
                        </Typography>
                      </Grid2>
                      <Grid2 size={12}>
                        <Divider />
                      </Grid2>
                      {/* Remaining amount is calculated by subtracting the max limit from the balance */}
                      <Grid2 size={6}>
                        <Typography variant="h5">
                          Remaining for addition
                        </Typography>
                      </Grid2>
                      <Grid2 size={6}>
                        <Typography variant="h5">
                          {remaining > 0 ? (
                            <>${remaining}</>
                          ) : (
                            <>-${Math.abs(remaining)}</>
                          )}
                        </Typography>
                      </Grid2>
                    </Grid2>
                  </CardContent>
                </Card>
              </Grid2>

              {/* Grid2 element with card inside to display transactions. Sizes are adjusted for different screen sizes with breakpoints. */}
              <Grid2 size={{ xs: 12, md: 7 }}>
                <Card variant="outlined" sx={{ boxShadow: 3 }}>
                  <CardHeader
                    title={
                      <>
                        <Stack
                          direction="row"
                          sx={{ justifyContent: "space-between" }}
                        >
                          <Typography variant="h5" sx={{ fontWeight: "bold" }}>
                            Transactions
                          </Typography>
                          {/* Filter and Options buttons */}
                          
                          <Button id="categoryButton" onClick={()=>{setFilterMenu(true)}}>Filter</Button>
                          {envelope.user !=null ?
                          <Button
                            onClick={() => {
                              setTransactionMenu(true);
                            }}
                            id="newButton"
                            variant="contained"
                          >
                            Options
                          </Button>
                          : <></>}
                          {/* Menu for creating transactions */}
                          <Menu
                            open={transactionMenu}
                            onClose={() => {
                              setTransactionMenu(false);
                            }}
                            anchorEl={document.getElementById("newButton")}
                            anchorOrigin={{
                              vertical: "bottom",
                              horizontal: "left",
                            }}
                            transformOrigin={{
                              vertical: "top",
                              horizontal: "left",
                            }}
                          >
                            <MenuItem
                              onClick={() => {
                                navigate("/add");
                              }}
                            >
                              Add Money
                            </MenuItem>
                            <MenuItem
                              onClick={() => {
                                handleCreateOpen();
                                setTransactionMenu(false);
                              }}
                            >
                              Spend Money
                            </MenuItem>
                            <MenuItem sx={{ color: "red" }}
                              onClick={() => {
                                setDeleteDialog(true);
                              }}
                            >
                              Delete Envelope
                            </MenuItem>
                          </Menu>

                          {/* Dialog for deleting envelope */}
                          <Dialog open={deleteDialog} onClose={() => {setDeleteDialog(false);}}>
                            <DialogTitle>Delete Envelope</DialogTitle>
                            <DialogContent>
                              <DialogContentText>
                                Are you sure you want to delete this envelope?
                              </DialogContentText>
                            </DialogContent>
                            <DialogActions>
                              <Button
                                onClick={() => {
                                  deleteEnvelope();
                                }}
                              >
                                Yes
                              </Button>
                              <Button
                                onClick={() => {
                                  setDeleteDialog(false);
                                }}
                              >
                                No
                              </Button>
                            </DialogActions>
                          </Dialog>

                          {/* Menu for filtering transactions */}
                           <Menu
                            open={filterMenu}
                            onClose={() => {
                              setFilterMenu(false);
                            }}
                            anchorEl={document.getElementById("categoryButton")}
                            anchorOrigin={{
                              vertical: "bottom",
                              horizontal: "left",
                            }}
                            transformOrigin={{
                              vertical: "top",
                              horizontal: "left",
                            }}
                          >
                            <MenuItem sx={{ fontWeight: "bold" }}
                              onClick={() => {
                                setFilteredCategory("All");
                                setFilterMenu(false);
                              }}>All</MenuItem>
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
                        </Stack>
                      </>
                    }
                  />

                  {/* mapping out transactions to display in accordions. Sorting by transaction_id to display most recent transactions first. */}
                  <CardContent sx={{ overflowY: "auto", maxHeight: "350px" }}>
                    {transactions.length === 0 ? (
                      <Typography variant="h4" sx={{ textAlign: "center" }}>
                        No transactions.{" "}
                      </Typography>
                    ) : (
                      transactions
                        .filter((transaction) => transaction.category === filteredCategory || filteredCategory === "All")
                        .sort(
                          (a, b) => b.datetime.getTime() - a.datetime.getTime()
                        )
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
                                        {Math.abs(
                                          transaction.transactionAmount
                                        )}
                                      </Typography>
                                    )}
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
                              </Stack>
                            </AccordionDetails>
                          </Accordion>
                        ))
                    )}
                  </CardContent>
                </Card>
              </Grid2>
              
              {/* Grid2 element with card inside to display balance history graph */}
              <Grid2 size={{ xs: 12, md: 5 }}>
                <Card variant="outlined" sx={{ boxShadow: 3 }}>
                  <CardHeader
                    title={
                      <>
                        <Typography variant="h5" sx={{ fontWeight: "bold" }}>
                          Balance History
                        </Typography>
                      </>
                    }
                  />
                  <CardContent>
                    <Box sx={{ width: "100%", height: "315px" }}>
                      <LineChart
                        series={[
                          {
                            data: envelopeHistory.map((history) => {
                              return history.envelopeAmount;
                            }),
                            color: "#8b4dfe",
                          },
                        ]}
                      />
                    </Box>
                  </CardContent>
                </Card>
              </Grid2>
            </Grid2>

            {/* Dialog for creating transactions */}
            <Dialog
              open={openCreate}
              onClose={() => {
                handleCreateClose();
              }}
              fullWidth
              maxWidth="md"
            >
              <DialogTitle>
                Spending from {envelope.envelopeDescription}
              </DialogTitle>
              <DialogContent>
                <DialogContentText>Enter details below.</DialogContentText>
                <br />
                <Stack direction="column" spacing={2}>
                  <TextField
                    id="createTitle"
                    name="title"
                    label="Title"
                    required
                    autoFocus
                    onChange={changeCreateTransactionValues}
                  />
                  <TextField
                    id="createTransactionDescription"
                    name="transactionDescription"
                    label="Description"
                    multiline
                    required
                    maxRows={5}
                    onChange={changeCreateTransactionValues}
                  />
                  <TextField
                    error={createAmountError}
                    id="createTransactionAmount"
                    name="transactionAmount"
                    label="Amount"
                    type="number"
                    slotProps={{
                      input: {
                        startAdornment: (
                          <InputAdornment position="start">$</InputAdornment>
                        ),
                      },
                    }}
                    helperText={
                      createAmountError
                        ? "Amount exceeds envelope balance."
                        : ""
                    }
                    required
                    onChange={changeCreateTransactionValues}
                  />
                  <TextField
                    id="createTransactionCategory"
                    name="category"
                    label="Category"
                    defaultValue={transactiontoCreate.category}
                    multiline
                    maxRows={5}
                    required
                    onChange={changeCreateTransactionValues}
                  />
                </Stack>
              </DialogContent>
              <DialogActions>
                <Button
                  onClick={() => {
                    handleCreateClose();
                  }}
                >
                  Cancel
                </Button>
                <Button
                  disabled={
                    createAmountError ||
                    transactiontoCreate.title === "" ||
                    transactiontoCreate.transactionAmount === 0 ||
                    transactiontoCreate.transactionDescription === "" ||
                    transactiontoCreate.category === "" 
                  }
                  type="submit"
                  onClick={(e) => {
                    createTransaction(e);
                    handleCreateClose();
                  }}
                >
                  Submit
                </Button>
              </DialogActions>
            </Dialog>

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
                <DialogContentText></DialogContentText>
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
          </>
        )}
      </>
    );
  
}
