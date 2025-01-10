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
import useStore, { UserInfo } from "./stores";

function App() {
  const setUser = useStore((state) => state.setUser);
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
          <Route path="/en/:id" element={<DetailedEnvelope />} />
          <Route path="/add" element={<AddMoney />} />
          <Route path="/users" element={<SeeUsers />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
