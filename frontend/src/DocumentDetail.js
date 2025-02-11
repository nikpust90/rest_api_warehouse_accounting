import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

const DocumentDetail = () => {
    const { id } = useParams();
    const [document, setDocument] = useState(null);

    useEffect(() => {
        axios.get(`http://localhost:8080/api/incoming/documents/${id}`)
            .then(response => setDocument(response.data))
            .catch(error => console.error("Ошибка загрузки документа", error));
    }, [id]);

    if (!document) return <p>Загрузка...</p>;

    return (
        <div>
            <h2>{document.name}</h2>
            <table border="1">
                <thead>
                <tr>
                    <th>Товар</th>
                    <th>Количество</th>
                    <th>Цена</th>
                </tr>
                </thead>
                <tbody>
                {document.items.map((item, index) => (
                    <tr key={index}>
                        <td>{item.productName}</td>
                        <td>{item.quantity}</td>
                        <td>{item.price}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default DocumentDetail;
