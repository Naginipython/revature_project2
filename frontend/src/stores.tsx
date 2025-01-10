import { create } from "zustand";

// Define user interface
export interface UserInfo {
  loggedIn: boolean;
  userId: number;
  username: string;
  role: string;
  firstName: string;
  lastName: string;
  token: string;
}

// Define store functions and state interface
interface StoreFuncs {
  setUser: (newUser: UserInfo) => void;
  setSnackbar: (open: boolean, message: string) => void;
}

interface StoreInterface {
  user: UserInfo;
  snackbar: {
    open: boolean;
    message: string;
  }
}

// Zustand store with persistence logic
const useStore = create<StoreInterface & StoreFuncs>()((set) => ({
  user: {
    loggedIn: false,
    userId: -1,
    username: "",
    role: "",
    firstName: "",
    lastName: "",
    token: "",
  },
  snackbar: {
    open: false,
    message: "",
  },

  setUser: (newUser) => {
    if (newUser.loggedIn) {
      // Store user info in localStorage
      localStorage.setItem("gooderBudgetUser", JSON.stringify(newUser));
      localStorage.setItem("gooderBudgetToken", newUser.token);
    } else {
      // Clear localStorage on logout
      localStorage.removeItem("gooderBudgetUser");
      localStorage.removeItem("gooderBudgetToken");
    }

    set(() => ({ user: newUser }));
  },
  setSnackbar: (open, message) => {
    set(() => ({ snackbar: { open, message } }));
  }
}));

export default useStore;
