import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Navbar } from "./Components/Navbar/Navbar";
import { Login } from "./Components/Auth/Login";
import { Register } from "./Components/Auth/Register";
import { DetailedEnvelope } from "./Components/DetailedEnvelope/DetailedEnvelope";
import { Personalize } from "./Components/Auth/Personalize";
import { AddMoney } from "./Components/AddMoney/AddMoney";
import { EnvelopeList } from "./Components/Envelopes/EnvelopeList";
import { CreateEnvelope } from "./Components/CreateEnvelope/CreateEnvelope";
import { useEffect } from "react";
import SeeUsers from "./Components/SeeUsers/SeeUsers";
import useStore from "./stores";
import { Alert, Snackbar } from "@mui/material";
import { AllTransactions } from "./Components/Transactions/AllTransactions";

function App() {
  const setUser = useStore((state) => state.setUser);
  const snackbar = useStore((state) => state.snackbar);
  const setSnackbar = useStore((state) => state.setSnackbar);
  // Login on page refresh
  useEffect(() => {
    const token = localStorage.getItem("gooderBudgetToken");

    if (token) {
      // Parse stored user information (if any)
      const userInfo = JSON.parse(
        localStorage.getItem("gooderBudgetUser") || "{}"
      );

      if (userInfo && userInfo.token) {
        setUser({
          loggedIn: true,
          ...userInfo,
        });
      }
    }
  }, [setUser]);

  const handleCloseSnackbar = () => {
    setSnackbar(false, "");
  };

  return (
    <>
      <BrowserRouter>
        <Navbar />
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/personalize" element={<Personalize />} />
          <Route path="/new_envelope" element={<CreateEnvelope />} />
          <Route path="/envelopes" element={<EnvelopeList />} />
          <Route path="/envelope/:id" element={<DetailedEnvelope />} />
          <Route path="/transactions" element={<AllTransactions />} />
          <Route path="/add" element={<AddMoney />} />
          <Route path="/users" element={<SeeUsers />} />
          <Route path="/transactions" element = {<AllTransactions />} />
        </Routes>
      </BrowserRouter>
      <Snackbar
        open={snackbar.open}
        autoHideDuration={1500}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={handleCloseSnackbar} severity="success" sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  );
}

export default App;
