import axios from 'axios';

const BASE_URL = 'https://backendmongo-tau.vercel.app';

/**
 * Fetch organizer profile details.
 * @param authToken - The authorization token.
 * @returns A promise resolving to the organizer profile details.
 */
export async function fetchOrganizerProfile(authToken: string) {
    try {
        const response = await axios.get(`${BASE_URL}/api/organizers/auth/profile`, {
            headers: {
                Authorization: `Bearer ${authToken}`,
            },
        });
        return response.data.data.organizer;
    } catch (error) {
        console.error('Error fetching organizer profile:', error);
        throw error;
    }
}
