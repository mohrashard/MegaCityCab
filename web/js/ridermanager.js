document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
            
            if (tabId === 'current-rides') {
                loadCurrentRides();
            } else if (tabId === 'ended-rides') {
                loadEndedRides();
            }
        });
    });
    
    loadCurrentRides();

    const dateFilter = document.querySelector('.filter-input[type="date"]');
    const nameFilter = document.querySelector('.filter-input[placeholder="Search by name"]');
    const sortSelect = document.querySelector('.filter-input[placeholder="Sort by earnings"]');
    
    if (dateFilter) {
        dateFilter.addEventListener('change', function() {
            filterEndedRides();
        });
    }
    
    if (nameFilter) {
        nameFilter.addEventListener('input', function() {
            filterEndedRides();
        });
    }
    
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            filterEndedRides();
        });
    }
});

function loadCurrentRides() {
    showLoading();
    
    fetch('/CabSystem/currentRides', { 
        headers: { 
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(`HTTP ${response.status}: ${text}`);
            });
        }
        
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error(`Invalid content type: ${contentType}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            displayCurrentRides(data.rides);
        } else {
            showNotification(data.message || 'Failed to load rides', 'error');
            showEmptyState();
        }
    })
    .catch(error => {
        console.error('Fetch error:', error);
        if (error.message.includes('Failed to fetch')) {
            showNotification('Server unavailable. Please try again later.', 'error');
        } else if (error.message.includes('HTTP 401')) {
            showNotification('Session expired. Please login again.', 'error');
            setTimeout(() => window.location.href = '/megacitycab/login.html', 2000);
        } else {
            showNotification('Error loading rides: ' + error.message, 'error');
        }
        showEmptyState();
    });
}

function showEmptyState() {
    const container = document.querySelector('#current-rides .ride-grid');
    container.innerHTML = '<div class="no-rides">No rides available</div>';
}

function displayCurrentRides(rides) {
    const container = document.querySelector('#current-rides .ride-grid');
    container.innerHTML = '';
    
     const acceptedRides = JSON.parse(localStorage.getItem('acceptedRides') || '[]');
    
    if (!rides || rides.length === 0) {
        container.innerHTML = '<div class="no-rides">No current rides available</div>';
        return;
    }
    
    rides.forEach(ride => {
                if (acceptedRides.includes(ride.bookingId)) {
            ride.driverAccepted = true;
        }
        ride.hireFee = Number(ride.hireFee || 0);
        ride.adminCharge = Number(ride.adminCharge || 0);
        ride.driverEarnings = Number(ride.driverEarnings || 0);
        
        const statusClass = getStatusClass(ride.status);
        const statusText = getStatusText(ride.status);
        
  
        const isPending = ride.status.toLowerCase() === 'pending';
        const isAssigned = ride.status.toLowerCase() === 'assigned';
        const isAccepted = ride.driverAccepted === true || ride.driverAccepted === "true"; 
        
        const rideCard = document.createElement('div');
        rideCard.className = 'ride-card';
        rideCard.setAttribute('data-booking-id', ride.bookingId);
        
        const formattedDate = formatDate(ride.bookingDatetime);
        
        rideCard.innerHTML = `
            <div class="ride-status ${statusClass}">${statusText}</div>
            <div class="passenger-name">${ride.passengerName || 'Unknown'}</div>
            <div class="phone-number">
                <a href="tel:${ride.passengerPhone}">
                    <i class="fas fa-phone"></i> ${ride.passengerPhone || 'N/A'}
                </a>
            </div>
            <div class="location-info">
                <div class="location-label">
                    <i class="fas fa-map-marker-alt" style="color: green"></i>
                    Pickup Location
                </div>
                <div class="location-address">${ride.pickupLocation || 'N/A'}</div>
            </div>
            <div class="location-info">
                <div class="location-label">
                    <i class="fas fa-map-marker-alt" style="color: red"></i>
                    Drop-off Location
                </div>
                <div class="location-address">${ride.dropoffLocation || 'N/A'}</div>
            </div>
            
            <div class="ride-datetime">
                <i class="far fa-calendar-alt"></i> ${formattedDate}
            </div>
                            <div class="payment-method">
                    <i class="fas fa-credit-card"></i> ${ride.paymentMethod || 'Cash'}
                </div>
            <div class="fee-breakdown">
                <div class="fee-item total">
                    <span>Total Fee:</span>
                    <span>LKR ${ride.hireFee.toFixed(2)}</span>
                </div>
                <div class="fee-item">
                    <span>Admin Charge (30%):</span>
                    <span>LKR ${ride.adminCharge.toFixed(2)}</span>
                </div>
                <div class="fee-item earnings">
                    <span>Driver Earnings:</span>
                    <span>LKR ${ride.driverEarnings.toFixed(2)}</span>
                </div>
            </div>
            <div class="action-buttons">
                ${isAssigned && !isAccepted ? 
                    `<button class="btn btn-accept" onclick="acceptRide(${ride.bookingId})">
                        <i class="fas fa-check"></i> Accept Ride
                    </button>` : ''}
                <button class="btn btn-cancel" onclick="showCancelModal(${ride.bookingId})">
                    <i class="fas fa-times"></i> Cancel
                </button>
                ${isAssigned && isAccepted ? 
                    `<button class="btn btn-end" onclick="showEndRideModal(${ride.bookingId})">
                        <i class="fas fa-flag-checkered"></i> End Ride
                    </button>` : ''}
            </div>
        `;
        
        container.appendChild(rideCard);
    });
}

function loadEndedRides() {
    fetch('/CabSystem/driver/ended-rides', {  
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(rides => {
        displayEndedRides(rides);
    })
    .catch(error => {
        console.error('Error loading ended rides:', error);
        showNotification('Failed to load ended rides. Please try again.', 'error');
    });
}

function displayEndedRides(rides) {
    const container = document.querySelector('#ended-rides .ride-grid');
    container.innerHTML = '';
    
    if (!rides || rides.length === 0) {
        container.innerHTML = '<div class="no-rides">No ended rides available</div>';
        return;
    }
    
    rides.forEach(ride => {
        ride.hireFee = Number(ride.hireFee || 0);
        ride.adminCharge = Number(ride.adminCharge || 0);
        ride.driverEarnings = Number(ride.driverEarnings || 0);
        
        const rideCard = document.createElement('div');
        rideCard.className = 'ride-card';

        const formattedDate = formatDate(ride.bookingDatetime);
        
        rideCard.innerHTML = `
            <div class="ride-status status-completed">Completed</div>
            <div class="passenger-name">${ride.passengerName || 'Unknown'}</div>
            <div class="phone-number">
                <a href="tel:${ride.passengerPhone}">
                    <i class="fas fa-phone"></i> ${ride.passengerPhone || 'N/A'}
                </a>
            </div>
            <div class="location-info">
                <div class="location-label">
                    <i class="fas fa-map-marker-alt" style="color: green"></i>
                    Pickup Location
                </div>
                <div class="location-address">${ride.pickupLocation || 'N/A'}</div>
            </div>
            <div class="location-info">
                <div class="location-label">
                    <i class="fas fa-map-marker-alt" style="color: red"></i>
                    Drop-off Location
                </div>
                <div class="location-address">${ride.dropoffLocation || 'N/A'}</div>
            </div>
            <div class="ride-info">
                <div class="ride-datetime">
                    <i class="far fa-calendar-alt"></i> ${formattedDate}
                </div>
                <div class="payment-method">
                    <i class="fas fa-credit-card"></i> ${ride.paymentMethod || 'Cash'}
                </div>
            </div>
            <div class="fee-breakdown">
                <div class="fee-item total">
                    <span>Total Fee:</span>
                    <span>LKR ${ride.hireFee.toFixed(2)}</span>
                </div>
                <div class="fee-item">
                    <span>Admin Charge (30%):</span>
                    <span>LKR ${ride.adminCharge.toFixed(2)}</span>
                </div>
                <div class="fee-item earnings">
                    <span>Driver Earnings:</span>
                    <span>LKR ${ride.driverEarnings.toFixed(2)}</span>
                </div>
            </div>
        `;
        
        container.appendChild(rideCard);
    });
}


function acceptRide(bookingId) {

    showNotification('Ride accepted successfully!', 'success');
    

    const rideCard = document.querySelector(`.ride-card[data-booking-id="${bookingId}"]`);
    if (rideCard) {

        const acceptButton = rideCard.querySelector('.btn-accept');
        if (acceptButton) {
            acceptButton.remove();
        }
        

        const actionButtons = rideCard.querySelector('.action-buttons');
        if (actionButtons) {
            const endRideButton = document.createElement('button');
            endRideButton.className = 'btn btn-end';
            endRideButton.innerHTML = '<i class="fas fa-flag-checkered"></i> End Ride';
            endRideButton.onclick = function() { showEndRideModal(bookingId); };
            actionButtons.appendChild(endRideButton);
        }
    }
    

    const acceptedRides = JSON.parse(localStorage.getItem('acceptedRides') || '[]');
    if (!acceptedRides.includes(bookingId)) {
        acceptedRides.push(bookingId);
        localStorage.setItem('acceptedRides', JSON.stringify(acceptedRides));
    }
}


function cancelRide(bookingId, reason) {
    fetch('/CabSystem/driver/cancel-ride', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: `bookingId=${bookingId}&reason=${encodeURIComponent(reason)}`
    })
    .then(response => response.json())
    .then(result => {
        if (result.success) {
            showNotification('Ride cancelled successfully!', 'success');
            loadCurrentRides();
        } else {
            showNotification('Failed to cancel ride. Please try again.', 'error');
        }
    })
    .catch(error => {
        console.error('Error cancelling ride:', error);
        showNotification('An error occurred. Please try again.', 'error');
    });
}


function endRide(bookingId) {
    fetch('/CabSystem/driver/end-ride', {  
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: `bookingId=${bookingId}`
    })
    .then(response => response.json())
    .then(result => {
        if (result.success) {
            showNotification('Ride ended successfully!', 'success');
            loadCurrentRides();
            loadEndedRides();
        } else {
            showNotification('Failed to end ride. Please try again.', 'error');
        }
    })
    .catch(error => {
        console.error('Error ending ride:', error);
        showNotification('An error occurred. Please try again.', 'error');
    });
}


function filterEndedRides() {
    const dateFilter = document.querySelector('.filter-input[type="date"]').value;
    const nameFilter = document.querySelector('.filter-input[placeholder="Search by name"]').value.toLowerCase();
    const sortOption = document.querySelector('.filter-input[placeholder="Sort by earnings"]').value;
    
    fetch('/driver/ended-rides', {  
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(rides => {
        if (dateFilter) {
            const filterDate = new Date(dateFilter);
            rides = rides.filter(ride => {
                const rideDate = new Date(ride.bookingDatetime);
                return rideDate.toDateString() === filterDate.toDateString();
            });
        }
        
 
        if (nameFilter) {
            rides = rides.filter(ride => 
                ride.passengerName.toLowerCase().includes(nameFilter)
            );
        }
        

        if (sortOption === 'high') {
            rides.sort((a, b) => b.driverEarnings - a.driverEarnings);
        } else if (sortOption === 'low') {
            rides.sort((a, b) => a.driverEarnings - b.driverEarnings);
        }
        
        displayEndedRides(rides);
    })
    .catch(error => {
        console.error('Error filtering ended rides:', error);
    });
}


function showCancelModal(bookingId) {
    const existingModal = document.querySelector('.modal');
    if (existingModal) {
        existingModal.remove();
    }
    
   const modal = document.createElement('div');
modal.className = 'modal';
modal.innerHTML = `
    <div class="modal-content">
        <button class="modal-close">&times;</button>
        <h2>Cancel Ride</h2>
        <p>Please provide a reason for cancellation:</p>
        <textarea id="cancel-reason" rows="4" placeholder="Reason for cancellation..."></textarea>
        <div class="modal-action-btns">
            <button class="modal-cancel-btn" onclick="closeModal()">Cancel</button>
            <button class="modal-submit-btn" onclick="submitCancellation(${bookingId})">Submit Request</button>
        </div>
    </div>
`;


const style = document.createElement('style');
style.id = 'dynamic-modal-styles';
style.textContent = `
    .modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.6);
        display: none;
        align-items: center;
        justify-content: center;
        z-index: 1000;
        backdrop-filter: blur(5px);
    }
    
    .modal.active {
        display: flex;
        animation: modalFadeIn 0.3s ease-out;
    }
    
.modal-content {
    background: linear-gradient(145deg, rgba(250, 247, 232, 0.15), rgba(250, 239, 181, 0.15));
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    padding: 2rem;
    border-radius: 15px;
    width: 90%;
    max-width: 450px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    position: relative;
    transform: translateY(-50px);
    opacity: 0;
    animation: modalSlideIn 0.4s cubic-bezier(0.23, 1, 0.32, 1) forwards;
    border: 1px solid rgba(255, 255, 255, 0.1);
}



    
    @keyframes modalSlideIn {
        to {
            transform: translateY(0);
            opacity: 1;
        }
    }
    
    .modal-content h2 {
        color: #d4a700;
        margin-bottom: 1.5rem;
        font-size: 1.8rem;
        border-bottom: 2px solid #d4a700;
        padding-bottom: 0.5rem;
    }
    
    .modal-content p {
        color: #d4a700;
        margin-bottom: 1rem;
        line-height: 1.5;
    }
    
    .modal-close {
        position: absolute;
        top: 1rem;
        right: 1rem;
        font-size: 1.5rem;
        color: #e74c3c;
        cursor: pointer;
        transition: transform 0.2s ease;
        background: none;
        border: none;
        padding: 0;
    }
    
    .modal-close:hover {
        transform: scale(1.2);
        color: #c0392b;
    }
    
#cancel-reason {
    width: 100%;
    padding: 12px;
    border: 2px solid #d4a700;
    border-radius: 8px;
    margin: 1rem 0;
    resize: vertical;
    min-height: 100px;
    font-family: inherit;
    transition: all 0.3s ease;
    background: rgba(250, 247, 232, 0.15);
    box-shadow: 0 0 10px rgba(212, 167, 0, 0.4);
}

#cancel-reason:focus {
    outline: none;
    border-color: #d4a700;
    box-shadow: 0 0 15px rgba(212, 167, 0, 0.7), 
                0 0 30px rgba(212, 167, 0, 0.5); 
}

#cancel-reason::placeholder {
    color: #d4a700;
    opacity: 1; 
    transition: var(--transition);
}

#cancel-reason:focus::placeholder {
    color: rgba(212, 167, 0, 0.7);
}


    
    .modal-action-btns {
        display: flex;
        gap: 1rem;
        margin-top: 1.5rem;
        justify-content: flex-end;
    }
    


.modal-cancel-btn {
    background: #faf7e8;
    color: #333333;
    border: 2px solid #d4a700;
    padding: 0.8rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 10px rgba(212, 167, 0, 0.3);
}


.modal-submit-btn {
    background: linear-gradient(135deg, #d4a700, #b78a00);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(212, 167, 0, 0.3);
}



.modal-cancel-btn:hover {
    background: #dcdde1;
    transform: translateY(-1px);
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}


.modal-submit-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(212, 167, 0, 0.4);
    background: linear-gradient(135deg, #b78a00, #d4a700);
}



.modal-cancel-btn:focus {
    border-color: #d4a700;
    box-shadow: 0 0 10px #d4a700;
}


.modal-submit-btn:focus {
    box-shadow: 0 0 10px #3498db;
}


    
    @media (max-width: 480px) {
        .modal-content {
            width: 95%;
            padding: 1.5rem;
        }
        .modal-action-btns {
            flex-direction: column;
        }
        .modal-cancel-btn,
        .modal-submit-btn {
            width: 100%;
            text-align: center;
        }
    }
`;


document.head.appendChild(style);
document.body.appendChild(modal);


setTimeout(() => modal.classList.add('active'), 10);


modal.querySelector('.modal-close').addEventListener('click', closeModal);    
    document.body.appendChild(modal);
    

    setTimeout(() => {
        modal.style.display = 'flex';
    }, 10);
    

    const closeBtn = modal.querySelector('.close');
    closeBtn.addEventListener('click', closeModal);
    
  
    modal.addEventListener('click', function(event) {
        if (event.target === modal) {
            closeModal();
        }
    });
}

function submitCancellation(bookingId) {
    const reason = document.getElementById('cancel-reason').value;
    
    if (!reason.trim()) {
        showNotification('Please provide a reason for cancellation.', 'warning');
        return;
    }
    
    cancelRide(bookingId, reason);
    closeModal();
}


function showEndRideModal(bookingId) {

    const existingModal = document.querySelector('.modal');
    if (existingModal) {
        existingModal.remove();
    }
    
const modal = document.createElement('div');
modal.className = 'modal';
modal.innerHTML = `
    <div class="modal-content">
        <button class="modal-close">&times;</button>
        <h2>End Ride</h2>
        <p>Are you sure you want to end this ride?</p>
        <div class="modal-action-btns">
            <button class="modal-cancel-btn" onclick="closeModal()">Cancel</button>
            <button class="modal-confirm-btn" onclick="confirmEndRide(${bookingId})">Confirm</button>
        </div>
    </div>
`;


if (!document.getElementById('dynamic-modal-styles')) {
    const style = document.createElement('style');
    style.id = 'dynamic-modal-styles';
    style.textContent = `
        .modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.6);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            backdrop-filter: blur(5px);
        }
        
        .modal.active {
            display: flex;
            animation: modalFadeIn 0.3s ease-out;
        }
        
.modal-content {
    background: linear-gradient(145deg, rgba(250, 247, 232, 0.15), rgba(250, 239, 181, 0.15));
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    padding: 2rem;
    border-radius: 15px;
    width: 90%;
    max-width: 450px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    position: relative;
    transform: translateY(-50px);
    opacity: 0;
    animation: modalSlideIn 0.4s cubic-bezier(0.23, 1, 0.32, 1) forwards;
    border: 1px solid rgba(255, 255, 255, 0.1);
}
        
        @keyframes modalSlideIn {
            to { transform: translateY(0); opacity: 1; }
        }
        
    .modal-content h2 {
        color: #d4a700;
        margin-bottom: 1.5rem;
        font-size: 1.8rem;
        border-bottom: 2px solid #d4a700;
        padding-bottom: 0.5rem;
    }
    
    .modal-content p {
        color: #d4a700;
        margin-bottom: 1rem;
        line-height: 1.5;
    }
        
        .modal-close {
            position: absolute;
            top: 1rem;
            right: 1rem;
            font-size: 1.5rem;
            color: #e74c3c;
            cursor: pointer;
            transition: transform 0.2s ease;
            background: none;
            border: none;
            padding: 0;
        }
        
        .modal-close:hover {
            transform: scale(1.2);
            color: #c0392b;
        }
        
        .modal-action-btns {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
            justify-content: flex-end;
        }
        
    .modal-cancel-btn {
    background: #f1f2f6;
    color: #2d3436;
    border: 2px solid #dcdde1;
    padding: 0.8rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 10px rgba(220, 221, 225, 0.3);
}

.modal-confirm-btn {
    background: linear-gradient(135deg, #d4a700, #b78a00);
    color: white;
    border: none;
    padding: 0.8rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(212, 167, 0, 0.3);
}

.modal-cancel-btn:hover {
    background: #dcdde1;
    transform: translateY(-1px);
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
}

.modal-confirm-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(212, 167, 0, 0.4);
    background: linear-gradient(135deg, #b78a00, #d4a700);
}

        @media (max-width: 480px) {
            .modal-content { width: 95%; padding: 1.5rem; }
            .modal-action-btns { flex-direction: column; }
            .modal-cancel-btn, .modal-confirm-btn { width: 100%; }
        }
    `;
    document.head.appendChild(style);
}

document.body.appendChild(modal);
setTimeout(() => modal.classList.add('active'), 10);


modal.querySelector('.modal-close').addEventListener('click', closeModal);
    
    document.body.appendChild(modal);
    

    setTimeout(() => {
        modal.style.display = 'flex';
    }, 10);
    

    const closeBtn = modal.querySelector('.close');
    closeBtn.addEventListener('click', closeModal);
    

    modal.addEventListener('click', function(event) {
        if (event.target === modal) {
            closeModal();
        }
    });
}


function confirmEndRide(bookingId) {
    endRide(bookingId);
    closeModal();
}


function closeModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.style.opacity = '0';
        setTimeout(() => {
            modal.remove();
        }, 300);
    }
}


function showNotification(message, type) {

    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = message;
    
    document.body.appendChild(notification);
    

    setTimeout(() => {
        notification.style.opacity = '1';
    }, 10);
    

    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}


function getStatusClass(status) {
    switch (status.toLowerCase()) {
        case 'pending':
            return 'status-pending';
        case 'assigned':
            return 'status-active';
        case 'ended':
            return 'status-completed';
        default:
            return '';
    }
}


function getStatusText(status) {
    switch (status.toLowerCase()) {
        case 'pending':
            return 'Pending';
        case 'assigned':
            return 'Active';
        case 'ended':
            return 'Completed';
        default:
            return status;
    }
}


function formatDate(dateTimeStr) {
    const date = new Date(dateTimeStr);
    const options = { day: 'numeric', month: 'short', year: 'numeric' };
    const timeOptions = { hour: '2-digit', minute: '2-digit', hour12: true };
    
    
    return `${date.toLocaleDateString('en-IN', options)} • ${date.toLocaleTimeString('en-IN', timeOptions)}`;
}


function showLoading() {
    const container = document.querySelector('#current-rides .ride-grid');
    container.innerHTML = '<div class="loading">Loading rides...</div>';
}
