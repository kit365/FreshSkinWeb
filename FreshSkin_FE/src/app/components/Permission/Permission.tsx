"use client"

export default function Permission(props: any) {
    const { permissions, roles } = props;

    return (
        <>
            {permissions.map((item: any, index: number) => (
                <tr key={index} data-name={item.dataName}>
                    <td>{item.dataContent}</td>
                    {roles.map((item: any, index: number) => (
                        <td key={index} style={{ textAlign: 'center' }} >
                            <input type="checkbox" data-id={item.roleId} />
                        </td>
                    ))}
                </tr>
            ))}

        </>
    )
}