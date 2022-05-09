import React from 'react';

const ResultItem = ({ result }) => {
    const { title, url, ir, pagerank, excerpt, score } = result;

    return (
        <div>
            <div class="content">
                <a class="header"><a href={url} target="_blank">{ title }</a></a>

                <div class="meta">
                    <span>{ url }</span>
                </div>


                <div class="description">
                    <p>{ excerpt }</p>
                </div>

            </div>
        </div>
    )
}

export default ResultItem;