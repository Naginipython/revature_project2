import axios from "axios";

const backendHost: string = "http://44.204.237.144:8080";
axios.defaults.baseURL = backendHost;
export default backendHost;