import React, { useState, useEffect } from 'react';

const ResultItem = ({ result }) => {
    const { title, url, ir, pagerank } = result;

    return (
        <div class="item">
            <div class="content">
                <a class="header"><a href={url} target="_blank">{ title }</a></a>

                <div class="meta">
                    <span>{ url }</span>
                </div>

                <div class="extra">
                    {ir}, {pagerank}
                </div>
            </div>
        </div>
    )
}

export default ResultItem;