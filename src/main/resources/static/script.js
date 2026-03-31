let currentSessionId = null;
const API_BASE = '/api/v1/interview';

async function startInterview() {
    const problemStatement = document.getElementById('problemStatement').value.trim();
    
    if (!problemStatement) {
        showError('Please enter a problem statement');
        return;
    }
    
    try {
        showLoading('Starting interview...');
        
        const response = await fetch(`${API_BASE}/start`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ problemStatement })
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const session = await response.json();
        currentSessionId = session.id;
        
        hideLoading();
        showSession(session);
        showSuccess('Interview started successfully!');
        
    } catch (error) {
        hideLoading();
        showError('Failed to start interview: ' + error.message);
    }
}

async function submitAnswer() {
    const answer = document.getElementById('answer').value.trim();
    
    if (!answer) {
        showError('Please enter an answer');
        return;
    }
    
    if (!currentSessionId) {
        showError('No active session');
        return;
    }
    
    try {
        showLoading('Submitting answer...');
        
        const response = await fetch(`${API_BASE}/${currentSessionId}/answer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ answer })
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const session = await response.json();
        
        hideLoading();
        updateSession(session);
        document.getElementById('answer').value = '';
        
    } catch (error) {
        hideLoading();
        showError('Failed to submit answer: ' + error.message);
    }
}

async function endInterview() {
    if (!currentSessionId) {
        showError('No active session');
        return;
    }
    
    try {
        showLoading('Ending interview...');
        
        const response = await fetch(`${API_BASE}/${currentSessionId}/end`, {
            method: 'PUT'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const session = await response.json();
        
        hideLoading();
        showResults(session);
        showSuccess('Interview completed!');
        
    } catch (error) {
        hideLoading();
        showError('Failed to end interview: ' + error.message);
    }
}

async function cancelInterview() {
    if (!currentSessionId) {
        showError('No active session');
        return;
    }
    
    try {
        showLoading('Cancelling interview...');
        
        const response = await fetch(`${API_BASE}/${currentSessionId}/cancel`, {
            method: 'PUT'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        hideLoading();
        resetInterface();
        showSuccess('Interview cancelled');
        
    } catch (error) {
        hideLoading();
        showError('Failed to cancel interview: ' + error.message);
    }
}

function showSession(session) {
    document.getElementById('startSection').classList.add('hidden');
    document.getElementById('sessionSection').classList.remove('hidden');
    updateSession(session);
}

function updateSession(session) {
    document.getElementById('sessionId').textContent = session.id;
    document.getElementById('sessionStatus').textContent = session.status;
    document.getElementById('sessionStatus').className = `status ${session.status.toLowerCase()}`;
    document.getElementById('sessionScore').textContent = session.score.toFixed(1);
    
    updateConversation(session.conversationLog);
    
    const isActive = session.status === 'ONGOING';
    document.getElementById('submitBtn').disabled = !isActive;
    document.getElementById('endBtn').disabled = !isActive;
    document.getElementById('cancelBtn').disabled = !isActive;
}

function updateConversation(conversationLog) {
    const conversation = document.getElementById('conversation');
    conversation.innerHTML = '';
    
    if (!conversationLog) return;
    
    const lines = conversationLog.split('\n').filter(line => line.trim());
    
    lines.forEach(line => {
        if (line.startsWith('Interviewer:')) {
            const message = document.createElement('div');
            message.className = 'message interviewer';
            message.innerHTML = `<strong>Interviewer</strong>${line.substring(12)}`;
            conversation.appendChild(message);
        } else if (line.startsWith('Candidate:')) {
            const message = document.createElement('div');
            message.className = 'message candidate';
            message.innerHTML = `<strong>Candidate</strong>${line.substring(10)}`;
            conversation.appendChild(message);
        }
    });
    
    conversation.scrollTop = conversation.scrollHeight;
}

function showResults(session) {
    document.getElementById('sessionSection').classList.add('hidden');
    document.getElementById('resultsSection').classList.remove('hidden');
    
    const results = document.getElementById('finalResults');
    results.innerHTML = `
        <div class="message">
            <h3>Final Score: ${session.score.toFixed(1)}/10</h3>
            <p><strong>Status:</strong> ${session.status}</p>
            ${session.feedback ? `<div style="margin-top: 15px;"><strong>Feedback:</strong><br><pre style="white-space: pre-wrap; font-family: inherit;">${session.feedback}</pre></div>` : ''}
        </div>
        <button onclick="resetInterface()" style="margin-top: 15px;">Start New Interview</button>
    `;
}

function resetInterface() {
    currentSessionId = null;
    document.getElementById('sessionSection').classList.add('hidden');
    document.getElementById('resultsSection').classList.add('hidden');
    document.getElementById('startSection').classList.remove('hidden');
    document.getElementById('answer').value = '';
    document.getElementById('problemStatement').value = '';
    clearMessages();
}

function showLoading(message) {
    clearMessages();
    const loading = document.createElement('div');
    loading.className = 'loading';
    loading.id = 'loadingMessage';
    loading.textContent = message;
    document.querySelector('.content').insertBefore(loading, document.querySelector('.content').firstChild);
}

function hideLoading() {
    const loading = document.getElementById('loadingMessage');
    if (loading) loading.remove();
}

function showError(message) {
    clearMessages();
    const error = document.createElement('div');
    error.className = 'error';
    error.textContent = message;
    document.querySelector('.content').insertBefore(error, document.querySelector('.content').firstChild);
    setTimeout(() => error.remove(), 5000);
}

function showSuccess(message) {
    clearMessages();
    const success = document.createElement('div');
    success.className = 'success';
    success.textContent = message;
    document.querySelector('.content').insertBefore(success, document.querySelector('.content').firstChild);
    setTimeout(() => success.remove(), 3000);
}

function clearMessages() {
    document.querySelectorAll('.error, .success, .loading').forEach(el => el.remove());
}

// Initialize event listeners when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Clean the problem statement textarea aggressively
    const problemTextarea = document.getElementById('problemStatement');
    
    // Force clean the textarea
    problemTextarea.value = '';
    problemTextarea.textContent = '';
    problemTextarea.innerHTML = '';
    
    // Reset any potential styling issues
    problemTextarea.style.textIndent = '0px';
    problemTextarea.style.paddingLeft = '12px';
    problemTextarea.style.textAlign = 'left';
    
    // Enable Enter key to submit answer
    document.getElementById('answer').addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') {
            submitAnswer();
        }
    });
    
    // Ensure cursor starts at beginning when focused
    problemTextarea.addEventListener('focus', function() {
        // Small delay to ensure the focus is complete
        setTimeout(() => {
            this.setSelectionRange(0, 0);
        }, 10);
    });
    
    // Also clean on input to prevent any weird characters
    problemTextarea.addEventListener('input', function() {
        // Remove any leading whitespace as user types
        if (this.value.length > 0 && this.value.charAt(0) === ' ') {
            this.value = this.value.trimStart();
            this.setSelectionRange(0, 0);
        }
    });
});