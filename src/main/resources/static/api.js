const API_BASE_URL = 'http://localhost:8080/api';

const api = {
    async get(endpoint, params = {}) {
        const url = new URL(`${API_BASE_URL}${endpoint}`);
        Object.keys(params).forEach(key => {
            if (params[key]) url.searchParams.append(key, params[key]);
        });

        const response = await fetch(url);
        if (!response.ok) throw new Error(await response.text());
        return response.json();
    },

    async post(endpoint, data) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(await response.text());
        if (response.status === 201) return true;
        return response.json();
    },

    async patch(endpoint, data) {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const errBody = await response.json();
            throw new Error(errBody.message || "Erro desconhecido");
        }
        return true;
    }
};
