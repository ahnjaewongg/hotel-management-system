let modal;
    let currentMode = 'add';
    let allRooms = [];

    function loadSidebar() {
      fetch('/html/components/sidebar.html')
        .then(response => response.text())
        .then(html => {
          document.getElementById('sidebarContainer').innerHTML = html;

          // 스크립트 실행
          const scriptContent = html.match(/<script id="sidebarScript">([\s\S]*?)<\/script>/);
          if (scriptContent && scriptContent[1]) {
            eval(scriptContent[1]);  // 스크립트 직접 실행

            // 현재 페이지 메뉴 활성화
            const roomLink = document.getElementById('nav-room');
            if (roomLink) {
              roomLink.classList.add('active');
            }
          }
        })
        .catch(error => console.error('Error loading sidebar:', error));
    }


    // 저장 로직을 별도 함수로 분리
    function handleSave() {
      const roomNumber = document.getElementById('roomNumber').value;
      const roomType = document.getElementById('roomType').value;
      const status = document.getElementById('roomStatus').value;
      const cleaningStatus = document.getElementById('cleaningStatus').value;
      const currentTime = new Date().toISOString();

      if (!roomNumber || !roomType || !status || !cleaningStatus) {
        alert('모든 필드를 입력해주세요.');
        return;
      }

      const data = {
        roomNumber,
        roomType,
        status,
        cleaningStatus,
        lastCleaned: cleaningStatus === 'clean' ? currentTime : null
      };

      const method = currentMode === 'add' ? 'POST' : 'PUT';
      const url = currentMode === 'add' ? '/api/room' : `/api/room/${roomNumber}`;

      fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      })
      .then(response => response.json())
      .then(result => {
        if (result.success) {
          alert(result.message);
          modal.hide();
          loadRooms();
        } else {
          alert(result.message || '작업에 실패했습니다.');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('오류가 발생했습니다.');
      });
    }

    function showAddModal() {
      currentMode = 'add';
      document.getElementById('modalTitle').textContent = '객실 추가';
      document.getElementById('roomForm').reset();
      document.getElementById('roomNumber').readOnly = false;
      modal.show();
    }

    // 객실 상태 원형 차트 생성/업데이트 함수
    let statusChart; // 차트 인스턴스를 전역 변수로 저장
    function updateStatusChart(stats) {
      const ctx = document.getElementById('statusChart').getContext('2d');
      const data = {
        labels: ['이용 가능', '사용 중', '점검 중', '예약됨'],
        datasets: [{
          data: [stats.available, stats.occupied, stats.maintenance, stats.reserved],
          backgroundColor: ['#198754', '#dc3545', '#ffc107', '#0dcaf0']
        }]
      };

      if (statusChart) {
        statusChart.destroy();
      }

      statusChart = new Chart(ctx, {
        type: 'pie',
        data: data,
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'bottom'
            }
          },
          onClick: (event, elements) => {
            if (elements && elements.length > 0) {
              const index = elements[0].index;
              const status = getStatusFromIndex(index);
              if (status) {
                filterRoomsByStatus(status);
              }
            }
          }
        }
      });
    }

    // getStatusFromIndex 함수 수정
    function getStatusFromIndex(index) {
      switch (index) {
        case 0: return 'available';
        case 1: return 'occupied';
        case 2: return 'maintenance';
        case 3: return 'reserved';
        default: return null;
      }
    }

    // updateDashboard 함수 수정
    function updateDashboard(rooms) {
      const stats = {
        total: rooms.length,
        available: rooms.filter(r => r.status === 'available').length,
        occupied: rooms.filter(r => r.status === 'occupied').length,
        maintenance: rooms.filter(r => r.status === 'maintenance').length,
        reserved: rooms.filter(r => r.status === 'reserved').length,
      };

      document.getElementById('totalRooms').textContent = stats.total;
      document.getElementById('availableRooms').textContent = stats.available;
      document.getElementById('occupiedRooms').textContent = stats.occupied;
      document.getElementById('maintenanceRooms').textContent = stats.maintenance;
      document.getElementById('reservedRooms').textContent = stats.reserved;

      updateStatusChart({
        available: stats.available,
        occupied: stats.occupied,
        maintenance: stats.maintenance,
        reserved: stats.reserved
      });

      const typeStats = {
        VIP: rooms.filter(r => r.roomType === 'VIP').length,
        SUITE: rooms.filter(r => r.roomType === 'SUITE').length,
        BUSINESS: rooms.filter(r => r.roomType === 'BUSINESS').length,
        NORMAL: rooms.filter(r => r.roomType === 'NORMAL').length
      };

      updateTypeChart(typeStats);

      // 청소 상태 통계
      const cleaningStats = {
        clean: rooms.filter(r => r.cleaningStatus === 'clean').length,
        dirty: rooms.filter(r => r.cleaningStatus === 'dirty').length,
        cleaning: rooms.filter(r => r.cleaningStatus === 'cleaning').length,
        inspection: rooms.filter(r => r.cleaningStatus === 'inspection').length
      };

      document.getElementById('cleanCount').textContent = cleaningStats.clean;
      document.getElementById('dirtyCount').textContent = cleaningStats.dirty;
      document.getElementById('cleaningCount').textContent = cleaningStats.cleaning;
    }

    // roomModal의 select 옵션에도 default 추가
    // HTML의 해당 부분을 수정

    // 상태별 필터링 함수 수정
    function filterRoomsByStatus(status) {
      clearAllAlerts(); // 모든 알림 제거

      const filteredRooms = status ? allRooms.filter(room => room.status === status) : allRooms;
      displayRooms(filteredRooms);

      // 필터 상태 표시
      const filterStatus = document.createElement('div');
      filterStatus.className = 'alert alert-info mt-2';
      filterStatus.innerHTML = status ?
        `${getStatusText(status)} 객실만 표시 중입니다. <button class="btn btn-sm btn-secondary ms-2" onclick="loadRooms()">전체 보기</button>` :
        '전체 객실을 표시 중입니다.';

      const tableContainer = document.querySelector('.table-responsive');
      tableContainer.insertBefore(filterStatus, tableContainer.firstChild);
    }

    // 객실 타입별 막대 차트 생성/업데이트 함수
    let typeChart; // 차트 인스턴스를 전역 변수로 저장
    function updateTypeChart(stats) {
      const ctx = document.getElementById('typeChart').getContext('2d');
      const data = {
        labels: ['VIP', 'SUITE', 'BUSINESS', 'NORMAL'],
        datasets: [{
          label: '객실 수',
          data: [stats.VIP, stats.SUITE, stats.BUSINESS, stats.NORMAL],
          backgroundColor: ['#4b0082', '#800080', '#4169e1', '#20b2aa']
        }]
      };

      if (typeChart) {
        typeChart.destroy();
      }

      typeChart = new Chart(ctx, {
        type: 'bar',
        data: data,
        options: {
          responsive: true,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1
              }
            }
          },
          onClick: (event, elements) => {
            if (elements && elements.length > 0) {
              const index = elements[0].index;
              const roomType = getRoomTypeFromIndex(index);
              if (roomType) {
                filterRoomsByType(roomType);
              }
            }
          }
        }
      });
    }

    function getRoomTypeFromIndex(index) {
      const types = ['VIP', 'SUITE', 'BUSINESS', 'NORMAL'];
      return types[index];
    }

    // 타입별 필터링 함수 수정
    function filterRoomsByType(roomType) {
      clearAllAlerts(); // 모든 알림 제거

      const filteredRooms = roomType ? allRooms.filter(room => room.roomType === roomType) : allRooms;
      displayRooms(filteredRooms);

      // 필터 상태 표시
      const filterStatus = document.createElement('div');
      filterStatus.className = 'alert alert-info mt-2';

      const typeMapping = {
        'VIP': 'VIP',
        'SUITE': 'SUITE',
        'BUSINESS': 'BUSINESS',
        'NORMAL': 'NORMAL'
      };

      filterStatus.innerHTML = roomType ?
        `${typeMapping[roomType]} 타입 객실만 표시 중입니다. <button class="btn btn-sm btn-secondary ms-2" onclick="loadRooms()">전체 보기</button>` :
        '전체 객실을 표시 중입니다.';

      const tableContainer = document.querySelector('.table-responsive');
      tableContainer.insertBefore(filterStatus, tableContainer.firstChild);
    }

    function loadRooms() {
      clearAllAlerts(); // 기존 알림 제거
      fetch('/api/room')
        .then(response => response.json())
        .then(rooms => {
          allRooms = rooms;
          displayRooms(rooms, false); // false 파라미터 추가
          updateDashboard(rooms);
        })
        .catch(error => {
          console.error('Error:', error);
          alert('객실 목록을 불러오는데 실패했습니다.');
        });
    }

    function displayRooms(rooms, showCount = true) {
      const tbody = document.getElementById('roomTableBody');
      tbody.innerHTML = rooms.map(room => `
    <tr>
      <td>${room.roomNumber}</td>
      <td>${room.roomType}</td>
      <td><span class="badge ${getStatusBadgeClass(room.status)}">${getStatusText(room.status)}</span></td>
      <td>
        <div class="dropdown">
          <button class="btn btn-sm ${getCleaningBadgeClass(room.cleaningStatus)} dropdown-toggle" 
                  type="button" data-bs-toggle="dropdown">
            ${getCleaningStatusText(room.cleaningStatus)}
          </button>
          <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="#" onclick="updateCleaningStatus('${room.roomNumber}', 'clean')">청소 완료</a></li>
            <li><a class="dropdown-item" href="#" onclick="updateCleaningStatus('${room.roomNumber}', 'dirty')">청소 필요</a></li>
            <li><a class="dropdown-item" href="#" onclick="updateCleaningStatus('${room.roomNumber}', 'cleaning')">청소 중</a></li>
            <li><a class="dropdown-item" href="#" onclick="updateCleaningStatus('${room.roomNumber}', 'inspection')">점검 필요</a></li>
          </ul>
        </div>
      </td>
      <td>${room.lastCleaned ? new Date(room.lastCleaned).toLocaleString() : '기록 없음'}</td>
      <td>
        <button class="btn btn-sm btn-primary" onclick="editRoom('${room.roomNumber}')">
          <i class="fas fa-edit"></i>
        </button>
        <button class="btn btn-sm btn-danger" onclick="deleteRoom('${room.roomNumber}')">
          <i class="fas fa-trash"></i>
        </button>
      </td>
    </tr>
  `).join('');

      // showCount가 true일 때만 검색 결과 카운트 표시
      if (showCount) {
        const resultCount = rooms.length;
        const infoDiv = document.createElement('div');
        infoDiv.id = 'searchInfo';
        infoDiv.className = 'alert alert-info mt-2';
        infoDiv.textContent = `총 ${resultCount}개 객실이 표시되고 있습니다.`;

        const tableContainer = document.querySelector('.table-responsive');
        tableContainer.insertBefore(infoDiv, tableContainer.firstChild);
      }
    }

    // 공통으로 사용할 알림 제거 함수
    function clearAllAlerts() {
      const tableContainer = document.querySelector('.table-responsive');
      const existingAlerts = tableContainer.querySelectorAll('.alert');
      existingAlerts.forEach(alert => alert.remove());
    }

    function searchRooms() {
      const searchTerm = document.getElementById('roomSearch').value.toLowerCase();
      clearAllAlerts(); // 모든 알림 제거

      if (!searchTerm) {
        displayRooms(allRooms);
        return;
      }

      const filteredRooms = allRooms.filter(room =>
        room.roomNumber.toLowerCase().includes(searchTerm)
      );

      displayRooms(filteredRooms);
    }

    function editRoom(roomNumber) {
      currentMode = 'edit';
      document.getElementById('modalTitle').textContent = '객실 수정';
      document.getElementById('roomNumber').value = roomNumber;
      document.getElementById('roomNumber').readOnly = true;

      // 현재 객실 정보 가져오기
      fetch(`/api/room/${roomNumber}`)
        .then(response => response.json())
        .then(room => {
          if (room) {
            document.getElementById('roomType').value = room.roomType;
            document.getElementById('roomStatus').value = room.status;
            document.getElementById('cleaningStatus').value = room.cleaningStatus || 'clean';
            document.getElementById('lastCleaned').value = room.lastCleaned ? 
              new Date(room.lastCleaned).toISOString().slice(0, 16) : '';
            modal.show();
          } else {
            alert('객실 정보를 찾을 수 없습니다.');
          }
        })
        .catch(error => {
          console.error('Error:', error);
          alert('객실 정보를 불러오는데 실패했습니다.');
        });
    }

    function deleteRoom(roomNumber) {
      if (confirm('정말로 이 객실을 삭제하시겠습니까?')) {
        fetch(`/api/room/${roomNumber}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json'
          }
        })
          .then(response => response.json())
          .then(result => {
            console.log('Delete result:', result); // 디버깅용
            if (result.success) {
              alert(result.message);
              loadRooms(); // 목록 새로고침
            } else {
              alert(result.message || '삭제에 실패했습니다.');
            }
          })
          .catch(error => {
            console.error('Error:', error);
            alert('객실 삭제 중 오류가 발생했습니다.');
          });
      }
    }

    function getStatusBadgeClass(status) {
      switch (status.toLowerCase()) {
        case 'available': return 'bg-success';
        case 'occupied': return 'bg-danger';
        case 'maintenance': return 'bg-warning';
        case 'reserved': return 'bg-info';
        default: return 'bg-secondary';
      }
    }

    function getStatusText(status) {
      switch (status.toLowerCase()) {
        case 'occupied': return '사용 중';
        case 'maintenance': return '점검 ';
        case 'reserved': return '예약됨';
        default: return '이용 가능';
      }
    }

    // 청소 상태 뱃지 클래스
    function getCleaningBadgeClass(status) {
      switch (status) {
        case 'clean': return 'bg-success';
        case 'dirty': return 'bg-danger';
        case 'cleaning': return 'bg-warning';
        case 'inspection': return 'bg-info';
        default: return 'bg-secondary';
      }
    }

    // 청소 상태 텍스트
    function getCleaningStatusText(status) {
      switch (status) {
        case 'clean': return '청소 완료';
        case 'dirty': return '청소 필요';
        case 'cleaning': return '청소 중';
        case 'inspection': return '점검 필요';
        default: return '상태 없음';
      }
    }

    // 청소 상태 변경 함수 수정
    function updateCleaningStatus(roomNumber, newStatus) {
      // 현재 객실 정보를 찾습니다
      const room = allRooms.find(r => r.roomNumber === roomNumber);
      
      if (!room) {
        alert('객실 정보를 찾을 수 없습니다.');
        return;
      }

      // 청소 상태 업데이트를 위한 별도의 엔드포인트 사용
      fetch(`/api/room/${roomNumber}/cleaning-status`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          cleaningStatus: newStatus
        })
      })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(result => {
        if (result.success) {
          console.log('Cleaning status updated successfully:', result);
          loadRooms();  // 전체 목록 새로고침
        } else {
          throw new Error(result.message || '청소 상태 업데이트에 실패했습니다.');
        }
      })
      .catch(error => {
        console.error('Error updating cleaning status:', error);
        alert(error.message || '청소 상태 업데이트 중 오류가 발생했습니다.');
      });
    }

    document.addEventListener('DOMContentLoaded', function () {
      loadSidebar();
      modal = new bootstrap.Modal(document.getElementById('roomModal'));
      loadRooms();
      document.getElementById('saveButton').addEventListener('click', handleSave);
    });