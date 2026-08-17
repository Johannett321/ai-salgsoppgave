import {ChatMessage} from "./ChatMessage";

export interface ChatConversation {
    id: string;
    messages?: ChatMessage[];
}