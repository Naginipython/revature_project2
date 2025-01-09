import { create } from 'zustand';

// Only fetch necessary fields
export interface UserInfo {
	loggedIn: boolean,

	userId: number,
	username: string,
	role: string,
	firstName: string,
	lastName: string,
	token: string,
}

interface storeFuncs {
	setUser: (newUser: UserInfo) => void,
}

interface storeInterface {
	user: UserInfo,
}

const useStore = create<storeInterface & storeFuncs>()((set) => ({
	user: {
		loggedIn: false,
	
		userId: -1,
		username: "",
		role: "",
		firstName: "",
		lastName: "",
		token: ""
	},

	// There's probably a better way to do this lmfao
	setUser: (newUser) => set((state) => ({
		user: newUser
	})),

}));

export default useStore;