import React, { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import Header from './Header';
import MainPage from './MainPage';

const App = () => {
    return (
        <div>
            <MainPage />
        </div>
    );
}

export default App;