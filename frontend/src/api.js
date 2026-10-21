import axios from 'axios'

const client = axios.create({
  withCredentials: true
})

export function login(payload) {
  return client.post('/api/auth/login', payload).then(res => res.data)
}

export function register(payload) {
  return client.post('/api/auth/register', payload).then(res => res.data)
}

export function fetchMe() {
  return client.get('/api/users/me').then(res => res.data)
}

export function createPost(payload) {
  return client.post('/api/posts', payload).then(res => res.data)
}

export function listFeed(params) {
  return client.get('/api/feed', { params }).then(res => res.data)
}

export function toggleLike(postId) {
  return client.post(`/api/posts/${postId}/likes/toggle`).then(res => res.data)
}

export function addComment(postId, payload) {
  return client.post(`/api/posts/${postId}/comments`, payload).then(res => res.data)
}

export function listComments(postId) {
  return client.get(`/api/posts/${postId}/comments`).then(res => res.data)
}

export async function uploadMedia(file) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('/api/media', {
        method: 'POST',
        // Do NOT set 'Content-Type': 'multipart/form-data'. 
        // fetch will automatically set it along with the correct boundary.
        body: formData,
        // headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` } // Uncomment if using JWT
    });

    if (!response.ok) {
        throw new Error('File upload failed');
    }

    return response.json(); // Returns { mediaUrl: "/media/..." }
}