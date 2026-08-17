import {User} from "./User";
import {SalgsoppgaveJob} from "./SalgsoppgaveJob";
import {ChatConversation} from "./ChatConversation";

export interface UserSalgsoppgaveJob {
    id: string;
    user: User;
    salgsoppgaveJob: SalgsoppgaveJob
    chatConversation?: ChatConversation
}