export interface History {
	amount_history_id: number,
	envelope_id: number,
	transaction_id: number,
	envelope_amount: number
}

export interface Envelope {
	envelope_id: number,
	user_id: number,
	envelope_description: string,
	balance: number,
	max_limit: number,
	envelope_history: History[],
	amount: string,
	setAmount: React.Dispatch<React.SetStateAction<string>>
}