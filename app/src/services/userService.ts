import { api } from 'boot/axios';
import type { User } from 'components/models';

export const userService = {
  updateProfile: (payload: Partial<Pick<User, 'nome' | 'telefone' | 'dataNascimento'>>) =>
    api.put<User>('/api/users/me', payload).then((r) => r.data),
};