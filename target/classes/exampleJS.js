console.log("JavaScript is running");

function generatePDF(locationId, data) {
    console.log("generatePDF function is being called");

    // jsPDF is now directly in the global scope thanks to the script tag
    const doc = new jspdf.jsPDF();

    doc.text('Lignes de Location (ID Location: ' + locationId + ')', 10, 10);

    // Safely Parse the data from a string to an array
    let parsedData;
    try {
        parsedData = JSON.parse(data);
    } catch (e) {
        console.error("Error parsing data: " + e);
        // Handle the error gracefully (e.g., display an error message to the user)
        parsedData = [["Error", "Parsing", "Data", "Failed"]];  // Placeholder Data
    }

    console.log('Starting autotable');

    doc.autoTable({
        head: [['ID Ligne', 'Nom Matériel', 'Quantité', 'Montant Total']],
        body: parsedData // Use the parsed data
    });

    console.log('Finished autotable');
    console.log('Saving PDF');

    doc.save('lignes_location.pdf');
    console.log('PDF saved');
}