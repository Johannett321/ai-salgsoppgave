export interface User {
    id: string
    email: string;

    firstName: string;
    lastName: string;
    profileImageUrl: string;

    lastLogin: Date;
    completedOnboarding: boolean
}