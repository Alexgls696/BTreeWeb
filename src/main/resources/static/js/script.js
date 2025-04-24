async function sendRequest(method, endpoint, body = null) {
    const statusIndicator = document.getElementById('statusIndicator');
    statusIndicator.className = 'status-indicator';
    statusIndicator.style.display = 'none';

    try {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(`http://localhost:8080/api/rows/${endpoint}`, options);
        const data = await response.json(); // Всегда парсим JSON

        if (!response.ok) {
            const error = new Error(data.error || 'Unknown error');
            error.status = response.status;
            throw error;
        }

        // Show success indicator
        statusIndicator.innerHTML = '✓';
        statusIndicator.classList.add('success', 'animate__animated', 'animate__bounceIn');
        statusIndicator.style.display = 'flex';

        // Hide after 2 seconds
        setTimeout(() => {
            statusIndicator.classList.remove('animate__bounceIn');
            statusIndicator.classList.add('animate__fadeOut');
            setTimeout(() => {
                statusIndicator.style.display = 'none';
                statusIndicator.classList.remove('animate__fadeOut', 'success');
            }, 500);
        }, 2000);

        changeResultTextBox("Запрос выполнен успешно",true);
        return data;
    } catch (error) {
        console.error(error);
        changeResultTextBox(error.message,false);

        // Show error indicator
        statusIndicator.innerHTML = '✗';
        statusIndicator.classList.add('error', 'animate__animated', 'animate__shakeX');
        statusIndicator.style.display = 'flex';

        // Hide after 3 seconds
        setTimeout(() => {
            statusIndicator.classList.remove('animate__shakeX');
            statusIndicator.classList.add('animate__fadeOut');
            setTimeout(() => {
                statusIndicator.style.display = 'none';
                statusIndicator.classList.remove('animate__fadeOut', 'error');
            }, 500);
        }, 3000);

        throw error;
    }
}

async function addRow() {
    try {
        const row = document.getElementById("inputRow").value;
        await sendRequest("POST", "add", { row });
        await findAll();
    } catch (error) {
        console.error("Error adding row:", error);
    }
}

async function removeRow() {
    try {
        const row = document.getElementById("inputRow").value;
        await sendRequest("DELETE", "delete", { row });
        await findAll();
    } catch (error) {
        console.error("Error removing row:", error);
    }
}

async function clearTree() {
    try {
        let noContent = false;
        await sendRequest("DELETE", "clear", { noContent });
        await findAll();
    } catch (error) {
        console.error("Error removing row:", error);
    }
}

async function containsRow() {
    try {
        const row = document.getElementById("inputRow").value;
        const result = await sendRequest("POST", "contains", { row });
        document.getElementById("result").innerText = result ? `Строка ${row} содержится в дереве` : `Строка ${row} отсутствует в дереве`;
    } catch (error) {
        console.error("Error checking row:", error);
        document.getElementById("result").innerText = "Error checking row";
    }
}

async function findAll() {
    try {
        const result = await sendRequest("GET", "all");
        displayResults(result);
    } catch (error) {
        console.error("Error finding all:", error);
        document.getElementById("result").innerText = "Error retrieving rows";
    }
}

async function findBetween() {
    try {
        const str1 = document.getElementById("inputStr1").value;
        const str2 = document.getElementById("inputStr2").value;
        const result = await sendRequest("POST", "between", { str1, str2 });
        displayResults(result);
    } catch (error) {
        console.error("Error finding between:", error);
        document.getElementById("result").innerText = "Error finding rows between";
    }
}

async function findFirstAndLast() {
    try {
        const result = await sendRequest("GET", "first-last");
        displayResults(result);
    } catch (error) {
        console.error("Error finding first and last:", error);
        document.getElementById("result").innerText = "Error retrieving first and last rows";
    }
}

async function findIfEqualLength() {
    try {
        const row = document.getElementById("inputString").value;
        const result = await sendRequest("POST", "equal-length", { row });
        displayResults(result);
    } catch (error) {
        console.error("Error finding equal length:", error);
        document.getElementById("result").innerText = "Error finding equal length rows";
    }
}

async function findIfLessThan() {
    try {
        const row = document.getElementById("inputString").value;
        const result = await sendRequest("POST", "less-than", { row });
        displayResults(result);
    } catch (error) {
        console.error("Error finding less than:", error);
        document.getElementById("result").innerText = "Error finding rows with less length";
    }
}

async function findIfMoreThan() {
    try {
        const row = document.getElementById("inputString").value;
        const result = await sendRequest("POST", "more-than", { row });
        displayResults(result);
    } catch (error) {
        console.error("Error finding more than:", error);
        document.getElementById("result").innerText = "Error finding rows with more length";
    }
}

function displayResults(data) {
    const resultDiv = document.getElementById("result");

    if (Array.isArray(data)) {

        const formattedData = data.map(item => {
            try {
                if (typeof item === 'string') {
                    const parsed = JSON.parse(item);
                    return parsed.row || item;
                }
                return item.row || item;
            } catch (e) {
                return item;
            }
        });

        resultDiv.innerHTML = `<pre>${formattedData.join('\n')}</pre>`;
    }
    else if (typeof data === 'object' && data !== null) {
        resultDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
    }
    else {
        resultDiv.innerHTML = `<pre>${data}</pre>`;
    }
}

async function uploadFileAndShowResults(){
    await uploadFile();
    await findAll();
}

async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const statusIndicator = document.getElementById('statusIndicator');

    if (fileInput.files.length === 0) {
        alert('Please select a file first.');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const response = await fetch('http://localhost:8080/api/rows/upload-file', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const count = await response.json();

        // Show success message with count
        statusIndicator.innerHTML = `✓ Uploaded ${count} row(s)`;
        statusIndicator.classList.add('success', 'animate__animated', 'animate__bounceIn');
        statusIndicator.style.display = 'flex';

        setTimeout(() => {
            statusIndicator.classList.remove('animate__bounceIn');
            statusIndicator.classList.add('animate__fadeOut');
            setTimeout(() => {
                statusIndicator.style.display = 'none';
                statusIndicator.classList.remove('animate__fadeOut', 'success');
            }, 500);
        }, 3000);

    } catch (error) {
        console.error('File upload error:', error);

        statusIndicator.innerHTML = '✗ Upload failed';
        statusIndicator.classList.add('error', 'animate__animated', 'animate__shakeX');
        statusIndicator.style.display = 'flex';

        setTimeout(() => {
            statusIndicator.classList.remove('animate__shakeX');
            statusIndicator.classList.add('animate__fadeOut');
            setTimeout(() => {
                statusIndicator.style.display = 'none';
                statusIndicator.classList.remove('animate__fadeOut', 'error');
            }, 500);
        }, 3000);
    }
}

function changeResultTextBox(value,good){
    const field =   document.getElementById('message-text-box');
    if(good){
        field.style.color = "green";
    }else{
        field.style.color = "red";
    }
   field.value = value;
}

(async () =>{
    await findAll();
})();

